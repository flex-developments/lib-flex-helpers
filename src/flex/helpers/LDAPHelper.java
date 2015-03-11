/*
 * lib-flex-helpers-v010
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * 
 * Este programa es software libre; Usted puede usarlo bajo los terminos de la
 * licencia de software GPL version 2.0 de la Free Software Foundation.
 *
 * Este programa se distribuye con la esperanza de que sea util, pero SIN
 * NINGUNA GARANTIA; tampoco las implicitas garantias de MERCANTILIDAD o
 * ADECUACION A UN PROPOSITO PARTICULAR.
 * Consulte la licencia GPL para mas detalles. Usted debe recibir una copia
 * de la GPL junto con este programa; si no, escriba a la Free Software
 * Foundation Inc. 51 Franklin Street,5 Piso, Boston, MA 02110-1301, USA.
 */

package flex.helpers;

import flex.helpers.i18n.I18n;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * LDAPHelper
 * Clase para operaciones genericas sobre LDAP.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @version 1.0
 */
public class LDAPHelper {
    
    public static DirContext establishLDAPConnection(
        String ldapURL, 
        String ldapUser, 
        String ldapPass
    ) throws NamingException {
        //Establer parametros para conexion
        Hashtable<String, Object> env = new Hashtable<>();
            if(ldapUser != null) {
                env.put(Context.SECURITY_PRINCIPAL, ldapUser);
                env.put(Context.SECURITY_CREDENTIALS, ldapPass);
            }
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapURL);
            env.put("java.naming.ldap.attributes.binary", "objectSID");
            //Para debugging de errores
            //env.put("com.sun.jndi.ldap.trace.ber", System.err);
        
        //Establecer conexion
        return new InitialDirContext(env);
    }
    
    public static NamingEnumeration<SearchResult> getAccountsByName(
        DirContext ctx, 
        String ldapSearchBase, 
        String accountName
    ) throws NamingException {
        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + accountName + "))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        return ctx.search(ldapSearchBase, searchFilter, searchControls);
    }
    
    public static NamingEnumeration<SearchResult> getGroupBySID(
        DirContext ctx, 
        String ldapSearchBase, 
        String grupSID
    ) throws NamingException {
        
        String searchFilter = "(&(objectClass=group)(objectSid=" + grupSID + "))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        return ctx.search(ldapSearchBase, searchFilter, searchControls);
    }
    
    public static String getPrimaryGroupSID(SearchResult srLdapUser) throws NamingException {
        byte[] objectSID = (byte[])srLdapUser.getAttributes().get("objectSid").get();
        String strPrimaryGroupID = (String)srLdapUser.getAttributes().get("primaryGroupID").get();    
        String strObjectSid = decodeSID(objectSID);
        
        return strObjectSid.substring(0, strObjectSid.lastIndexOf('-') + 1) + strPrimaryGroupID;
    }
    
    public static void editLDAPObject (
        DirContext ctx, 
        String cannonicalObjectName,
        String property, 
        Object newValue
    ) throws NamingException {
        //Buscar atributo existente
        int aperation = DirContext.ADD_ATTRIBUTE;
        Attributes orig = ctx.getAttributes(cannonicalObjectName, new String[]{property});
        if(orig.size() > 0 ) aperation = DirContext.REPLACE_ATTRIBUTE;
        
        //Construir atributo que se modificara
        BasicAttribute attribute = new BasicAttribute(property, newValue);
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(aperation, attribute);
        
        //Modificar
        ctx.modifyAttributes(cannonicalObjectName, mods);
    }
    
    public static HashMap<String, String> editLDAPObjects (
        DirContext ctx, 
        String objectsPath,
        List<String> cannonicalObjectsName, 
        String property, 
        Object newValue
    ) {
        HashMap<String, String> traceResults = new HashMap<>();
        
        for(String cannonicalObjectName : cannonicalObjectsName) {
            //Modificar objeto
            try {
                editLDAPObject(ctx, cannonicalObjectName, property, newValue);
                
            } catch (NamingException ex) {
                //Ante un fallo se genera una traza
                String msj = I18n.get(I18n.L_LDAP_ATTRIBUTE_MODIFIED_ERROR, cannonicalObjectName, ex.getLocalizedMessage());
                traceResults.put(cannonicalObjectName, msj);
                break;
            }
            
            traceResults.put(cannonicalObjectName, Boolean.toString(true));
        }
        return traceResults;
    }
    
    public static List<Object> editLDAPObjectsByCN (
        DirContext ctx, 
        String objectsPath,
        List<String> objectsList, 
        String property, 
        Object newValue
    )  {
        
        List<Object> results = new ArrayList<>();
        
        for (int i = 0; i < objectsList.size(); i++) {
            try {
                SearchResult find = getAccountsByName(ctx, objectsPath, objectsList.get(i)).nextElement();
                if(find != null) {
                    objectsList.set(i, find.getNameInNamespace());
                    
                    //Modificar objeto
                    editLDAPObject(ctx, objectsList.get(i), property, newValue);
                    results.add(objectsList.get(i));
                    
                } else {
                    String msj = I18n.get(I18n.L_LDAP_ATTRIBUTE_MODIFIED_ERROR, 
                                           objectsList.get(i), 
                                           I18n.get(I18n.L_LDAP_ACCOUNT_NULL)
                    );
                    results.add(new Exception(msj));
                }
                
            } catch (NamingException ex) {
                String msj = I18n.get(I18n.L_LDAP_ATTRIBUTE_MODIFIED_ERROR, objectsList.get(i), ex.getLocalizedMessage());
                results.add(new Exception(msj));
            }
        }
        
        return results;
    }
    
    /**
     * The binary data is in the form:
     * byte[0] - revision level
     * byte[1] - count of sub-authorities
     * byte[2-7] - 48 bit authority (big-endian)
     * and then count x 32 bit sub authorities (little-endian)
     * 
     * The String value is: S-Revision-Authority-SubAuthority[n]...
     * 
     * Based on code from here - http://forums.oracle.com/forums/thread.jspa?threadID=1155740&tstart=0
     * @param sid
     * @return 
     */
    public static String decodeSID(byte[] sid) {
        final StringBuilder strSid = new StringBuilder("S-");

        // get version
        final int revision = sid[0];
        strSid.append(Integer.toString(revision));
        
        //next byte is the count of sub-authorities
        final int countSubAuths = sid[1] & 0xFF;
        
        //get the authority
        long authority = 0;
        //String rid = "";
        for(int i = 2; i <= 7; i++) {
           authority |= ((long)sid[i]) << (8 * (5 - (i - 2)));
        }
        strSid.append("-");
        strSid.append(Long.toHexString(authority));
        
        //iterate all the sub-auths
        int offset = 8;
        int size = 4; //4 bytes for each sub auth
        for(int j = 0; j < countSubAuths; j++) {
            long subAuthority = 0;
            for(int k = 0; k < size; k++) {
                subAuthority |= (long)(sid[offset + k] & 0xFF) << (8 * k);
            }
            
            strSid.append("-");
            strSid.append(subAuthority);
            
            offset += size;
        }
        
        return strSid.toString();    
    }
}
