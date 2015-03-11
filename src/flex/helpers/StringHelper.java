/*
 * lib-flex-helpers
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com | flopez@suscerte.gob.ve
 * Ing. Yessica De Ascencao - yessicadeascencao@gmail.com | ydeascencao@suscerte.gob.ve
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

import java.util.ArrayList;

/**
 * StringHelper
 * Clase para operaciones genericas sobre strings.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public final class StringHelper {
    
    public static String removeCarrierReturn(String string) {
        String result = "";
        if (string!=null) {
            int i = string.length();
            int j = 0;
            char jump = '\n';
            char tab = '\r';
            while(j<i) {
                if((string.charAt(j) != jump) && (string.charAt(j) != tab))
                    result = result + string.charAt(j);
                else
                    result = result + "";
                j++;
            }
        }
        return result;
    }
    
    public static String removeLastCarrierReturn(String string) {
        if ( (string.endsWith("\n")) || (string.endsWith("\r")) ) 
            string = string.substring(0, string.length()-1);
        
        return string;
    }
    
    public static String[] listToArrayString(ArrayList<String> list) {
        String[] array = new String[list.size()];
        for(String par : list) {
            array[list.indexOf(par)] = par;
        }
        return array;
    }
}
