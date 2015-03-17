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

import flex.helpers.exceptions.SMimeCoderHelperException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

/**
 * SMimeCoderHelper
 * Clase para operaciones genericas de codificacion y decodificacion SMime.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 */
public final class SMimeCoderHelper {
    final public static String ENCODER = "base64";
    
    public static String getSMimeEncoded(byte[] data) throws SMimeCoderHelperException {
        byte[] smime = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream encoded;
            encoded = MimeUtility.encode(baos, ENCODER);
            encoded.write(data);
            encoded.close();
            smime = baos.toByteArray();
            
        } catch (MessagingException | IOException ex) {
            throw new SMimeCoderHelperException(ex);
        }
        
        //Eliminar un retorno al final que siempre sobra
        return StringHelper.removeLastCarrierReturn(new String(smime));
    }

    public static byte[] getSMimeDecoded(String smime) throws SMimeCoderHelperException {
        InputStream b64is = null;
        byte[] res = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(smime.getBytes());
            try {
                b64is = MimeUtility.decode(bais, ENCODER);
            } catch (MessagingException ex) {
                throw new IOException(ex);
            }
            byte[] tmp = new byte[smime.getBytes().length];
            int n = b64is.read(tmp);
            res = new byte[n];
            System.arraycopy(tmp, 0, res, 0, n);
            
        } catch (IOException ex) {
            throw new SMimeCoderHelperException(ex);
            
        } finally {
            try {
                if (b64is != null) b64is.close();
            } catch (IOException ex) {
                throw new SMimeCoderHelperException(ex);
            }
        }
        return res;
    }
}
