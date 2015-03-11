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

import flex.helpers.i18n.I18n;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CSVHelper.
 * Clase para operaciones genericas sobre archivos en formato CSV.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public final class CSVHelper {
    final public static String fieldSeparator = I18n.get(I18n.F_CSV_FIELD_SEPARATOR);
    
    /**
     * Listar las lineas de un CSV.
     * @param csvFile Archivo CSV.
     * @return Lineas del archivo CSV.
     * @throws IOException Cuando no existe el archivo o cuando el usuario no 
     * tiene permiso para lectura.
     */
    public static List<String> getLines(File csvFile) throws IOException {
        if (!csvFile.exists())
            throw new IOException(I18n.get(I18n.M_FILE_NO_EXIST, csvFile.getPath()));
        if (!csvFile.canRead())
            throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_READ, csvFile.getPath()));
        
        List<String> lines = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(new FileInputStream(csvFile))) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            
            while ((line = br.readLine()) != null) lines.add(line);
        }
        
        return lines;
    }
    
    /**
     * Mapear el contenido de un archivo CSV en una lista de listas.
     * @param csvFile Archivo CSV.
     * @return Lista de listas con el contenido del archivo CSV.
     * @throws IOException Cuando no existe el archivo o cuando el usuario no 
     * tiene permiso para lectura.
     */
    public static List<List<String>> mapContent(File csvFile) throws IOException {
        if (!csvFile.exists())
            throw new IOException(I18n.get(I18n.M_FILE_NO_EXIST, csvFile.getPath()));
        if (!csvFile.canRead()) 
            throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_READ, csvFile.getPath()));
        
        List<List<String>> list = new ArrayList<>();

        List<String> lines = getLines(csvFile);
        boolean header = true;
        int max_size = 0;
        for(String line: lines) {
            List<String> aux = new ArrayList<>();
            String[] splitLine = line.split(fieldSeparator);
            aux.addAll(Arrays.asList(splitLine));
            
            if (header) {
                //Se supone que la primera linea es la cabecera
                max_size = aux.size();
                header = false;
                list.add(aux);
                
            } else if (aux.size() == max_size) {
                    list.add(aux);
            } else {
                throw new IOException(I18n.get(I18n.M_ERROR_CSV_PATTERN_ERROR, aux.size(), max_size));
            }
        }

        return list;
    }

    /**
     * Exportar una lista de listas a un archivo CSV.
     * @param csvFile Archivo CSV de salida.
     * @param csvMap Lista de listas con el contenido del archivo CSV.
     * @throws IOException cuando el usuario no tiene permiso para escritura.
     */
    public static void exportCSV(File csvFile, List<List<String>> csvMap) throws IOException {
        if (!csvFile.canRead()) 
            throw new IOException(I18n.get(I18n.M_FILE_PERMISSION_NO_WRITE, csvFile.getPath()));
        
        try (FileWriter writer = new FileWriter(csvFile)) {
            for (List<String> list : csvMap) {
                for (String s : list) {
                    writer.append(s);
                    writer.append(fieldSeparator);
                }
                writer.append('\n');
            }
        }
    }
    
    /**
     * Agregar una linea a un archivo CSV.
     * @param csvFile Archivo CSV.
     * @param line String que se desea agregar como linea adicional al archivo CSV.
     * @throws IOException Cuando se presenta algun problema durante la escritura.
     */
    public static void addToCSV(File csvFile, String line) throws IOException {
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append(line);
        }
    }
    
    /**
     * Agregar varias lineas a un archivo CSV.
     * @param csvFile
     * @param lines Lista de strings que se desean agregar como lineas adicionales
     * al archivo CSV.
     * @throws IOException Cuando se presenta algun problema durante la escritura.
     */
    public static void addToCSV(File csvFile, List<String> lines) throws IOException {
        try (FileWriter writer = new FileWriter(csvFile)) {
            for(String line: lines)
                writer.append(line);
        }
    }
    
    /**
     * Agregar una linea a un archivo CSV abierto.
     * @param writer Objeto que mantiene la escritura sobre el archivo CSV.
     * @param line String que se desea agregar como linea adicional al archivo CSV.
     * @throws IOException Cuando se presenta algun problema durante la escritura.
     */
    public static void addToOpenCSV(FileWriter writer, String line) throws IOException {
        writer.append(line);
        writer.flush();
    }
    
    /**
     * Agregar varias lineas a un archivo CSV abierto.
     * @param writer Objeto que mantiene la escritura sobre el archivo CSV.
     * @param lines Lista de strings que se desean agregar como lineas adicionales
     * al archivo CSV abierto.
     * @throws IOException Cuando se presenta algun problema durante la escritura.
     */
    public static void addToOpenCSV(FileWriter writer, List<String> lines) throws IOException {
        for(String line: lines)
            writer.append(line);
        writer.flush();
    }
}
