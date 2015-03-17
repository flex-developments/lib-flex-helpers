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

import flex.helpers.exceptions.NTPHelperException;
import flex.helpers.exceptions.VirtualClockHelperException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * VirtualClockHelper
 * Clase para gestionar reloj virtual.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public final class VirtualClockHelper {
    final public static String DATE_SOURCE_LOCAL = "LOCAL";
    final public static String DATE_SOURCE_NTP = "NTP";
    final public static String DATE_SOURCE_TSS = "TSS";
    
    private VirtualClockThreat clock = null;
    
    public VirtualClockHelper() {
        setLocalClock();
    }
            
    public VirtualClockHelper(
        List<String> ntpServers,
        int ntpDateInterval
    ) throws VirtualClockHelperException {
        setNTPClock(ntpServers, ntpDateInterval);
    }
    
    public String getDateSource() {
        return clock.getDateSource();
    }
    
    public Date getDate() throws VirtualClockHelperException {
        if(clock.isAlive()){
            return clock.getHora();
        } else {
            throw new VirtualClockHelperException(clock.getInternalException());
        }
    }
    
    public void setLocalClock() {
        killCurrentClock();
        clock = new VirtualClockThreat();
        clock.start();
    }
    
    public void setNTPClock(
        List<String> ntpServers,
        int ntpDateInterval
    ) throws VirtualClockHelperException {
        killCurrentClock();
        clock = new VirtualClockThreat(ntpServers, ntpDateInterval);
        clock.start();
    }
    
    private void killCurrentClock() {
        if(clock != null) {
            clock.interrupt();
            clock = null;
            System.gc();
        }
    }
}
    
/**
 * VirtualClockThreat
 * Clase para intentar emular el comportamiento de un reloj mediante hilos.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 */
final class VirtualClockThreat extends Thread {
    private String dateSource = VirtualClockHelper.DATE_SOURCE_LOCAL;
    private Throwable internalException;
    
    private int ntpDateInterval = 60000; //1 minute by default
    private List<String> ntpServers =  null;
    private String ntpResponder = null;
    
    private Date dateLocalReference = null;
    private Date dateNTPReference = null;
    
    public VirtualClockThreat() {
        this.internalException = null;
        this.dateSource = VirtualClockHelper.DATE_SOURCE_LOCAL;
    }
            
    public VirtualClockThreat(
        List<String> ntpServers,
        int ntpDateInterval
    ) throws VirtualClockHelperException {
        this.internalException = null;
        if(ntpServers.isEmpty())
            throw new VirtualClockHelperException(NTPHelperException.ERROR_NTP_EMPTY);
        this.ntpServers =  ntpServers;
        if(ntpDateInterval > 0) this.ntpDateInterval = ntpDateInterval;
        this.dateSource = VirtualClockHelper.DATE_SOURCE_NTP;
        
        try {
            updateNTPReference();

        } catch (NTPHelperException ex) {
            internalException = ex;
            throw new VirtualClockHelperException(internalException);
        }
    }
    
    public String getDateSource() {
        return dateSource;
    }
    
    public Throwable getInternalException() {
        return this.internalException;
    }
    
    public Date getHora() {
        //OJO... Aquí falta validar contra desplazamiento del tiempo del Thread
        if(getDateSource().compareToIgnoreCase(VirtualClockHelper.DATE_SOURCE_LOCAL) == 0) { //Si la fuente es la hora local
            return new Date(System.currentTimeMillis());
            
        } else { //Si la fuente es la hora del NTP se debe calcular la hora aproximada
            Date current = new Date(System.currentTimeMillis());
            long diff = current.getTime() - dateLocalReference.getTime();
            return new Date(dateNTPReference.getTime() + diff);
        }
    }
    
    private void updateNTPReference() throws NTPHelperException {
        Map<String, Date> result = NTPHelper.getServerAndDateTime(ntpServers, this.ntpResponder);
        Iterator it = result.entrySet().iterator();
        Map.Entry e = (Map.Entry)it.next();

        this.ntpResponder = (String) e.getKey();
        this.dateLocalReference = new Date(System.currentTimeMillis());
        this.dateNTPReference = (Date) e.getValue();
    }
    
    @Override
    public void run() {
        if(dateSource.compareTo(VirtualClockHelper.DATE_SOURCE_NTP) == 0) {
            try {
                updateNTPReference();
                
            } catch (NTPHelperException ex) {
                internalException = ex;
                Thread.interrupted();
            }
        }
        
        this.threatSleep(ntpDateInterval);
    }
    
    private void threatSleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
