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

package flex.helpers.exceptions;

import flex.helpers.i18n.I18n;

/**
 * NTPHelperException
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class NTPHelperException extends Exception {
    final public static String ERROR_NTP_UNKNOWN_HOST = I18n.get(I18n.M_ERROR_HOST_UNKNOWN);
    final public static String ERROR_NTP_SOCKET = I18n.get(I18n.M_ERROR_NTP_SOCKET);
    final public static String ERROR_NTP_READ = I18n.get(I18n.M_ERROR_NTP_READ);
    final public static String ERROR_NTP_CONECTION = I18n.get(I18n.M_ERROR_NTP_CONECTION);
    final public static String ERROR_NTP_EMPTY = I18n.get(I18n.M_ERROR_NTP_EMPTY);
            
    public NTPHelperException(String message) {
        super(message);
    }
    
    public NTPHelperException(Throwable e) {
        super(e);
    }
    
    public NTPHelperException(String message, Throwable e) {
        super(message, e);
    }
}
