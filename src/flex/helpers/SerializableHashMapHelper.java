/**
 * Clase originalmente desarrollada por:
 * Joseph Lewis <joehms22@gmail.com>
 * 
 * Fuente original: "http://onehourhacks.blogspot.com/2012/04/serializable-hash-map-in-java.html"
 * Original Package: none
 * ---
 * Modificación apoyada por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments en gmail | flopez en suscerte gob ve
 * Ing. Yessica De Ascencao - yessicadeascencao@gmail.com | ydeascencao en suscerte gob ve
 */
package flex.helpers;

import flex.helpers.exceptions.SMimeCoderHelperException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * A HashMap that serializes in to a base64 representation.
 * @author Joseph Lewis <joehms22@gmail.com>
 *
 * @param <E>
 * @param <T>
 */
public class SerializableHashMapHelper<E extends Serializable,T extends Serializable> extends HashMap<E,T> implements Serializable {
    private static final long serialVersionUID = 1L;
 
    /**
     * Creates an empty serializable hash map.
     */
    public SerializableHashMapHelper(){}

    /**
     * Creates a new map from a pre-serialized representation.
     * @param init
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws flex.helpers.exceptions.SMimeCoderHelperException
     */
    public SerializableHashMapHelper(String init) throws IOException, ClassNotFoundException, SMimeCoderHelperException {
        SerializableHashMapHelper<E,T> et = deserialize(init);
        putAll(et);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        // Write number of objects to follow
        out.writeInt(size());

        // Write Key:Value pairs.
        for(E key : keySet()) {
            out.writeObject(key);
            out.writeObject(get(key));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Read number of objects to follow
        int num = in.readInt();

        // Read key value pairs.

        for(int i = 0; i < num; i++) {
            @SuppressWarnings("unchecked")
            E key = (E) in.readObject();
            @SuppressWarnings("unchecked")
            T val = (T) in.readObject();
            put(key, val);
        }  
    }

    @Override
    public String toString() {
        String output = this.getClass().getSimpleName() + "\n";
        for(E key : keySet())
            output += "\t" + key.toString() + " -> " + get(key) + "\n";
        output = StringHelper.removeLastCarrierReturn(output);
        
        return output;
    }
    
    /**
     * Same as serialize, but returns the given string if serialization fails.
     * @param given
     * @return 
     */
    public String safeSerialize(String given) {
        try {
            return serialize();
            
        } catch (IOException | SMimeCoderHelperException e) {
            return given;
        }
    }
    
    /**
     * Creates a serialized representation of this object.
     * 
     * @return
     * @throws IOException
     * @throws flex.helpers.exceptions.SMimeCoderHelperException
     */
    public String serialize() throws IOException, SMimeCoderHelperException {
        ObjectOutput out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        out = new ObjectOutputStream(bos) ;
        out.writeObject(this);
        out.close();

        // Get the bytes of the serialized object
        byte[] buf = bos.toByteArray();
        String result = SMimeCoderHelper.getSMimeEncoded(buf);
        
        return StringHelper.removeLastCarrierReturn(result);
    }

    /**
     * Returns a deserialized representation of an object.
     * @param s
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private SerializableHashMapHelper<E,T> deserialize(String s) throws IOException, ClassNotFoundException, SMimeCoderHelperException {
        byte[] buf = SMimeCoderHelper.getSMimeDecoded(s);

        ByteArrayInputStream bosin = new ByteArrayInputStream(buf);
        ObjectInput in = new ObjectInputStream(bosin);

        return (SerializableHashMapHelper<E, T>) in.readObject();
    }
}
