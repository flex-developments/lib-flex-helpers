/*
 * lib-flex-helpers
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com | flopez@suscerte.gob.ve
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

package flex.helpers.test;

import flex.helpers.SMimeCoderHelper;

/**
 * SMimeCoderHelperTest
 *
 * @author Ing. Felix D. Lopez M. - fdmarchena2003@hotmail.com
 * @version 1.0
 */
public class SMimeCoderHelperTest {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        byte[] data = "test".getBytes();
        
        String resultA = SMimeCoderHelper.getSMimeEncoded(data);
        System.out.println(resultA);
        
        byte[] decoded = SMimeCoderHelper.getSMimeDecoded(resultA);
        System.out.println(new String (decoded));
        
        System.out.println("End!");
        System.exit(0);
    }
}
