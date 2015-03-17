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

import flex.helpers.exceptions.DateHelperException;
import flex.helpers.exceptions.LoggerHelperException;
import flex.helpers.i18n.I18n;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

/**
 * LoggerHelper
 * Clase para generar trazas de logs con formato.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class LoggerHelper {
    final public static int LOG_TYPE_SINGLE = 1;
    final public static int LOG_TYPE_SINGLE_DATED = 2;
    final public static int LOG_TYPE_MULTILINE = 3;
    
    final public static int LOG_FORMATER_HANDLER_PATTERN_SIMPLE = 1;
    final public static int LOG_FORMATER_HANDLER_PATTERN_XML = 2;
    
    private static Logger logger = null;
    private int LogType = 1;
    
    public LoggerHelper(String name, int type) {
        try {
            logger = Logger.getLogger(name);
            setLogType(LogType);
        } catch (LoggerHelperException ex) {
        }
    }

    public final void setLogType(int LogType) throws LoggerHelperException {
        if( (LogType < LOG_TYPE_SINGLE) || (LogType > LOG_TYPE_MULTILINE) )
            throw new LoggerHelperException(LoggerHelperException.ERROR_UNKNOWN_LOG_TYPE + LogType);
        
        this.LogType = LogType;
    }
    
    
    /**
     * 
     * @param name
     * @param type
     * @param handlerPattern Ruta o patron para archivo de logs. 
     * Ejemplo: "MiLog.log" ó "%t/MiLog.log" (Usa la carpeta de temporales)
     * @param formatHandlerPattern
     * @throws IOException 
     */
    public LoggerHelper(String name, int type, String handlerPattern, int formatHandlerPattern)
    throws IOException {
        logger = Logger.getLogger(name);
        LogType = type;
        addHandler(handlerPattern, formatHandlerPattern);
    }
    
    public final void addHandler(String pattern, int formatHandlerPattern) throws IOException {
        Formatter formatHandler = null;
        switch(formatHandlerPattern){
            case LOG_FORMATER_HANDLER_PATTERN_SIMPLE:
                formatHandler = new SimpleFormatter();
                break;
            case LOG_FORMATER_HANDLER_PATTERN_XML:
                formatHandler = new XMLFormatter();
                break;
            default:
                formatHandler = new SimpleFormatter();
                break;
        }
        
        Handler fh = new FileHandler(pattern);
        fh.setFormatter(formatHandler);
        logger.addHandler(fh);
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public void writeInfoLog(String message) {
        logger.info( generateLog(message) );
    }
    
    public void writeWarningLog(String message) {
        logger.warning( generateLog(message) );
    }
    
    public void writeErrorLog(String message) {
        logger.severe( generateLog(message) );
    }
    
    public void writeErrorLog(Throwable ex) {
        logger.severe(generateLog(ex.getClass().getName() + ": " + ex.getMessage()));
    }
    
    private String generateLog(String originalMessage) {
        try {
            switch (LogType) {
                case LOG_TYPE_SINGLE:
                    return I18n.get(I18n.F_LOG_TYPE_SINGLE, originalMessage);

                case LOG_TYPE_SINGLE_DATED:
                    return I18n.get(I18n.F_LOG_TYPE_SINGLE_DATED, 
                                     DateHelper.dateToString(new Date()), 
                                     originalMessage);
                    
                case LOG_TYPE_MULTILINE:
                    return I18n.get(I18n.F_LOG_TYPE_MULTILINE, 
                                     DateHelper.dateToString(new Date()), 
                                     originalMessage);
            }
            
        } catch (DateHelperException ex) {
            //Nothing...
        }
        return null;
    }
}
