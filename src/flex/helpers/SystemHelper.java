/*
 * lib-flex-helpers
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments en gmail
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments en gmail | flopez en suscerte gob ve
 * Ing. Yessica De Ascencao - yessicadeascencao@gmail.com | ydeascencao en suscerte gob ve
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

import flex.helpers.exceptions.SystemHelperException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * SystemHelper
 * Clase para operaciones genericas sobre el sistema operativo.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public final class SystemHelper {
    final public static String OS_ID_WINDOWS = "WINDOWS";
    final public static String OS_ID_LINUX = "LINUX";
    
    public static boolean isWindows() throws SystemHelperException {
        return SystemHelper.getOS().compareToIgnoreCase(SystemHelper.OS_ID_WINDOWS) == 0;
    }
    
    public static boolean isLinux() throws SystemHelperException {
        return SystemHelper.getOS().compareToIgnoreCase(SystemHelper.OS_ID_LINUX) == 0;
    }
            
    public static String getOS() throws SystemHelperException {
        String so = System.getProperty("os.name").toUpperCase();
        if(so.contains(OS_ID_WINDOWS))    return OS_ID_WINDOWS;    
        if(so.contains(OS_ID_LINUX) )     return OS_ID_LINUX;
            
        throw new SystemHelperException(SystemHelperException.ERROR_SO_UNSUPPORTED);
    }
    
    public static String execCommand(ArrayList<String> params) throws IOException {
        String [] command = StringHelper.listToArrayString(params);
        
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader br =new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        String line = "";
        String resp = "";
        while ((line = br.readLine()) != null) {
            resp = resp + "\n" + line;
        }
        return resp;
    }
    
    public static String getEnvironmentVar(String environmentVar) {
        return System.getenv(environmentVar);
    }
    
    public static String getUserHomeDirectory() {
        return System.getProperty("user.home");
    }

    public static String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }
}
