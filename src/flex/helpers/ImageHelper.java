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

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * ImageHelper
 * Clase para operaciones genericas sobre archivos de imagen.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public final class ImageHelper {
    
    /**
     * Metodo que convierte una cadena de bytes en una imagen JPG.
     * @param bytes Array que contiene los binarios de la imagen.
     * @param extension Extensión correspondiente al formato de la imagen.
     * @return
     * @throws IOException 
     */
    public static Image byteArrayToImage(byte[] bytes, String extension) throws IOException {
       ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
       Iterator readers = ImageIO.getImageReadersByFormatName(extension);    
       ImageReader reader = (ImageReader) readers.next();
       Object source = bis;
       ImageInputStream iis = ImageIO.createImageInputStream(source);
       reader.setInput(iis, true);
       ImageReadParam param = reader.getDefaultReadParam();
       return reader.read(0, param);
    }
}
