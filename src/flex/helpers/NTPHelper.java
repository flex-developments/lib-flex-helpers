/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---
 * Modificación apoyada por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com | flopez@suscerte.gob.ve
 * Ing. Yessica De Ascencao - yessicadeascencao@gmail.com | ydeascencao@suscerte.gob.ve
 */

package flex.helpers;

import flex.helpers.exceptions.NTPHelperException;
import flex.helpers.i18n.I18n;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
//import java.net.UnknownHostException;
//import java.text.NumberFormat;
//import org.apache.commons.net.ntp.NtpUtils;

/**
 * This is an example program demonstrating how to use the NTPUDPClient
 * class. This program sends a Datagram client request packet to a
 * Network time Protocol (NTP) service port on a specified server,
 * retrieves the time, and prints it to standard output along with
 * the fields from the NTP message header (e.g. stratum level, reference id,
 * poll interval, root delay, mode, ...)
 * See <A HREF="ftp://ftp.rfc-editor.org/in-notes/rfc868.txt"> the spec </A>
 * for details.
 * <p>
 * Usage: NTPClient <hostname-or-address-list>
 * <br>
 * Example: NTPClient clock.psu.edu
 *
 * @author Jason Mathews, MITRE Corp
 * ----
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 **/
public final class NTPHelper {
    final public static int NTP_TIMEOUT = 5000;
    
    /**
     * Solicitar hora y fecha a un pool de servidores NTP, donde se devolverá
     * solo la hora del primer servidor que responda. Adicionalmente se puede 
     * indicar un servidor como principal el cual se considerara como un favorito.
     * 
     * @param ntpServers Lista de Strings con el pool de servidores NTP.
     * @param principalNTPServer
     * @return Date con la respuesta del primer servidor que responda
     * @throws NTPHelperException 
     */
    public static Date getDateTime(
        List<String> ntpServers, 
        String principalNTPServer) 
    throws NTPHelperException {
        Map<String, Date> result = getServerAndDateTime(ntpServers, principalNTPServer);
        Iterator it = result.entrySet().iterator();
        Map.Entry e = (Map.Entry)it.next();
        
        return (Date) e.getValue();
    }
    
    /**
     * Solicitar hora y fecha a un pool de servidores NTP, donde se devolverá
     * un HashMap con el servidor y la hora del primer servidor que responda.
     * Adicionalmente se puede indicar un servidor como principal el cual se 
     * considerara como un favorito.
     * 
     * @param ntpServers Lista de Strings con el pool de servidores NTP.
     * @param principalNTPServer
     * @return Date con la respuesta del primer servidor que responda
     * @throws NTPHelperException 
     */
    public static Map<String, Date> getServerAndDateTime(
        List<String> ntpServers, 
        String principalNTPServer
    ) throws NTPHelperException {
        Map<String, Date> result = new HashMap<>();
        Date ntpTime = null;
        String trazaNTP = "";
        
        //Intentar obtener la hora desde el NTP principal
        if(principalNTPServer != null)
            try {
                ntpTime = getDateTime(principalNTPServer);
                result.put(principalNTPServer, ntpTime);
                return result;
                
            } catch (NTPHelperException ex) {
                //No logra obtener la hora. Genera traza e intenta con la lista
                trazaNTP = I18n.get(I18n.L_NTP_TRACE, principalNTPServer, ex.getLocalizedMessage());
            }
        
        //Validar la lista
        if(ntpServers == null)
            throw new NTPHelperException(NTPHelperException.ERROR_NTP_EMPTY);
        if(ntpServers.isEmpty())
            throw new NTPHelperException(NTPHelperException.ERROR_NTP_EMPTY);
        
        //Intenta obtener la hora desde cualquier servidor de la lista
        for (String ntpServer : ntpServers) {
            try {
                ntpTime = getDateTime(ntpServer);
                result.put(ntpServer, ntpTime);
                return result;
                
            } catch (NTPHelperException ex) {
                //No se logra la conexion NTP
                trazaNTP = trazaNTP + I18n.get(I18n.L_NTP_TRACE, ntpServer, ex.getLocalizedMessage());
            }
        }
        
        //No logra obtener la hora de ningun NTP.
        throw new NTPHelperException(trazaNTP);
    }
    
    /**
     * Solicitar hora y fecha a servidor NTP.
     * 
     * @param ntpServer Dirección ip del servidor NTP
     * @return
     * @throws NTPHelperException 
     */
    public static Date getDateTime(String ntpServer) throws NTPHelperException {
        if(ntpServer == null)
            throw new NTPHelperException(NTPHelperException.ERROR_NTP_EMPTY);
        if(ntpServer.isEmpty())
            throw new NTPHelperException(NTPHelperException.ERROR_NTP_EMPTY);
        
        Date time = null;
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(NTP_TIMEOUT);

        try {
            client.open();
            InetAddress hostAddr = InetAddress.getByName(ntpServer);
            TimeInfo info = client.getTime(hostAddr);
            time = processResponse(info);
            
        } catch (UnknownHostException ex) {
            throw new NTPHelperException(NTPHelperException.ERROR_NTP_UNKNOWN_HOST, ex.getCause());
            
        } catch (SocketException ex) {
            throw new NTPHelperException(NTPHelperException.ERROR_NTP_SOCKET, ex.getCause());

        } catch (IOException ex) {
            throw new NTPHelperException(NTPHelperException.ERROR_NTP_READ, ex.getCause());

        } finally {
            client.close();
            System.gc();
        }

        return time;
    }
    
    /**
     * Process <code>TimeInfo</code> object and print its details.
     * 
     * @param info <code>TimeInfo</code> object.
     * @return 
     */
    public static Date processResponse(TimeInfo info) {
        NtpV3Packet message = info.getMessage();
        org.apache.commons.net.ntp.TimeStamp rcvNtpTime = message.getReceiveTimeStamp(); //Receive Time is time request received by server (t2)
        return new Date(rcvNtpTime.getTime());
        //OJO... Para mejorar
//        TimeStamp refNtpTime = message.getReferenceTimeStamp();
//        TimeStamp origNtpTime = message.getOriginateTimeStamp(); //Originate Time is time request sent by client (t1)
//        TimeStamp xmitNtpTime = message.getTransmitTimeStamp(); //Transmit time is time reply sent by server (t3)
//        long destTime = info.getReturnTime();
//        TimeStamp destNtpTime = TimeStamp.getNtpTime(destTime); //Destination time is time reply received by client (t4)
//        int stratum = message.getStratum(); //Stratum should be 0..15...
//        String refType;
//        if (stratum <= 0) {
//            refType = "(Unspecified or Unavailable)";
//        } else if (stratum == 1) {
//            refType = "(Primary Reference; e.g., GPS)"; //GPS, radio clock, etc.
//        } else {
//            refType = "(Secondary Reference; e.g. via NTP or SNTP)";
//        }
//        
//        int version = message.getVersion();
//        int li = message.getLeapIndicator();
//        int poll = message.getPoll(); //poll value typically btwn MINPOLL (4) and MAXPOLL (14)
//        double disp = message.getRootDispersionInMillisDouble();
//        int refId = message.getReferenceId();
//        String refAddr = NtpUtils.getHostAddress(refId);
//        String refName = null;
//        if (refId != 0) {
//            if (refAddr.compareTo("127.127.1.0") == 0) {
//                refName = "LOCAL"; //This is the ref address for the Local Clock
//            } else if (stratum >= 2) {
//                //If reference id has 127.127 prefix then it uses its own reference clock
//                //defined in the form 127.127.clock-type.unit-num (e.g. 127.127.8.0 mode 5
//                //for GENERIC DCF77 AM; see refclock.htm from the NTP software distribution.
//                if (!refAddr.startsWith("127.127")) {
//                    try {
//                        InetAddress addr = InetAddress.getByName(refAddr);
//                        String name = addr.getHostName();
//                        if (name != null && !(name.compareTo(refAddr) == 0)) {
//                            refName = name;
//                        }
//                    } catch (UnknownHostException e) {
//                        //some stratum-2 servers sync to ref clock device but fudge stratum level higher... (e.g. 2)
//                        //ref not valid host maybe it's a reference clock name?
//                        //otherwise just show the ref IP address.
//                        refName = NtpUtils.getReferenceClock(message);
//                    }
//                }
//            } else if (version >= 3 && (stratum == 0 || stratum == 1)) {
//                refName = NtpUtils.getReferenceClock(message);
//                //refname usually have at least 3 characters (e.g. GPS, WWV, LCL, etc.)
//            }
//            //otherwise give up on naming the beast...
//        }
//        if (refName != null && refName.length() > 1) {
//            refAddr += " (" + refName + ")";
//        }
//        
//        info.computeDetails(); // compute offset/delay if not already done
//        Long offsetValue = info.getOffset();
//        Long delayValue = info.getDelay();
//        String delay = (delayValue == null) ? "N/A" : delayValue.toString();
//        String offset = (offsetValue == null) ? "N/A" : offsetValue.toString();
//        final NumberFormat numberFormat = new java.text.DecimalFormat("0.00");
//          
//        System.out.println(" Stratum: " + stratum + " " + refType);
//        System.out.println(" leap=" + li + ", version=" + version + ", precision=" + message.getPrecision());
//        System.out.println(" mode: " + message.getModeName() + " (" + message.getMode() + ")");
//        System.out.println(" poll: " + (poll <= 0 ? 1 : (int) Math.pow(2, poll)) + " seconds" + " (2 ** " + poll + ")");
//        System.out.println(" rootdelay=" + numberFormat.format(message.getRootDelayInMillisDouble()) + ", rootdispersion(ms): " + numberFormat.format(disp));
//        System.out.println(" Reference Identifier:\t" + refAddr);
//        System.out.println(" Reference Timestamp:\t" + refNtpTime + "  " + refNtpTime.toDateString());
//        System.out.println(" Originate Timestamp:\t" + origNtpTime + "  " + origNtpTime.toDateString());
//        System.out.println(" Receive Timestamp:\t" + rcvNtpTime + "  " + rcvNtpTime.toDateString());
//        System.out.println(" Transmit Timestamp:\t" + xmitNtpTime + "  " + xmitNtpTime.toDateString());
//        System.out.println(" Destination Timestamp:\t" + destNtpTime + "  " + destNtpTime.toDateString());
//        System.out.println(" Roundtrip delay(ms)=" + delay + ", clock offset(ms)=" + offset); //offset in ms
    }
}
