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

import flex.helpers.exceptions.DateHelperException;
import flex.helpers.i18n.I18n;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateHelper
 * Clase para operaciones genericas sobre los datos tipo hora/fecha.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public final class DateHelper {
    final private static SimpleDateFormat format = new SimpleDateFormat(I18n.get(I18n.F_DATE));
    
    /**
     * Devuelve un string con la fecha formateada.
     * @param date Datos tipo hora/fecha.
     * @return String de fecha formateada.
     * @throws DateHelperException Cuando se presenta algún problema durante el
     * proceso de formato.
     */
    public static String dateToString(Date date) throws DateHelperException {
        if(date == null)
            throw new DateHelperException(DateHelperException.ERROR_STRING_TO_DATE_NULL);
        
        return format.format(date);
    }
    
    /**
     * Devuelve dato tipo hora/fecha a partir de un string formateado.
     * @param date string formateado.
     * @return Datos tipo hora/fecha.
     * @throws DateHelperException Cuando el string es nulo o no cumple con el formato.
     */
    public static Date stringToDate(String date) throws DateHelperException {
        if(date == null) date = "";
        if(date.isEmpty()) 
            throw new DateHelperException(DateHelperException.ERROR_STRING_TO_DATE_NULL);
        
        try {
            return format.parse(date);
            
        } catch (ParseException ex) {
            throw new DateHelperException(DateHelperException.ERROR_STRING_TO_DATE);
        }
    }
}
