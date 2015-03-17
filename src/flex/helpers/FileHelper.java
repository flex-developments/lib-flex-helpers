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

import flex.helpers.externals.com.itextpdf.text.io.StreamUtil;
import flex.helpers.i18n.I18n;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * FileHelper
 * Clase para operaciones genericas sobre archivos genericos.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public final class FileHelper {
    
    public static String getExtension(String filePath) {
        return (filePath.substring(filePath.lastIndexOf('.'), filePath.length()));
    }
    
    public static String getCheckSum(
        String filePath, 
        String hashAlgorithm
    ) throws NoSuchAlgorithmException, IOException {
        
        byte[] file = getBytes(filePath);
        
        //Calcular hash
        MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
        byte[] hash = md.digest(file);
        
        return HexCoderHelper.getStringHexEncoded(hash);
    }
    
    public static byte[] getBytes(String filePath) throws IOException {
        DataInputStream in = null;
        
        try {
            File file = new File(filePath);
            if (!file.exists()) 
                throw new IOException(I18n.get(I18n.M_FILE_NO_EXIST, file.getPath()));
            if (!file.canRead()) 
                throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_READ, file.getPath()));
            
            in = new DataInputStream(new FileInputStream(file));
            byte[] result = StreamUtil.inputStreamToArray(in, (int) file.length());
            in.close();
            return result;
            
        } finally {
            try {
                if(in != null) in.close();
            } catch (IOException ex) {
                throw ex;
            }
        }
    }
    
    public static void write(String filePath, byte[] content) throws IOException {
        File file = new File(filePath);
        
        //OJO... Verificar fallo de permisologia para escritura
//        if (!file.canWrite()) 
//            throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_WRITE, file.getPath()));
        
        FileOutputStream envfos = null;
        try {
            envfos = new FileOutputStream(filePath);
            envfos.write(content);
            envfos.close();
            
        } finally {
            try {
                envfos.close();
            } catch (IOException ex) {
                throw ex;
            }
        }
    }
    
    public static void copy(String inFilePath, String outFilePath) throws IOException {
        File inFile = new File(inFilePath);
        File outFile = new File(outFilePath);
        
        if (!inFile.exists()) 
            throw new IOException(I18n.get(I18n.M_FILE_NO_EXIST, inFile.getPath()));
        if (!inFile.canRead()) 
            throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_READ, inFile.getPath()));
        if (!outFile.canWrite()) 
            throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_WRITE, outFile.getPath()));
        
        InputStream in = new FileInputStream(inFile);
        OutputStream out = new FileOutputStream(outFile);
            
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static void delete(String filePath) throws IOException {
        File file = new File(filePath);
        
        if (!file.exists()) 
            throw new IOException(I18n.get(I18n.M_FILE_NO_EXIST, file.getPath()));
        if (!file.canWrite()) 
            throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_WRITE, file.getPath()));
        
        if (file.isDirectory()) {
            String[] files = file.list();
            if (files.length > 0)
                throw new IOException(I18n.get(I18n.M_DIRECTORY_NO_CONTENT, file.getPath()));
        }
        
        if(!file.delete()) 
            throw new IOException(I18n.get(I18n.M_ERROR_FILE_PERMISSION_NO_DELETE, file.getPath()));
    }
}
