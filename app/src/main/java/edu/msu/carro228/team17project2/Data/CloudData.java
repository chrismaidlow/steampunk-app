package edu.msu.carro228.team17project2.Data;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Data for storing game state to server
 */
public class CloudData implements Serializable {
    public static final transient int LOST = -1;
    public static final transient int PLAYING = 0;
    public static final transient int WON = 1;


    public PipeData[][] board;
    public PipeData[][] selector;
    public int turn = 0;
    public int state = 0;

    public static String Serialize(CloudData data) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(data);
            oo.flush();
            return Base64.encodeToString(bo.toByteArray(), 0);
        } catch (Exception e) {
            Log.e("Serialization Error", e.toString());
            return "";
        }
    }

    public static CloudData Deserialize(String data) {
        try {
            byte[] deser = Base64.decode(data, 0);
            ByteArrayInputStream bo = new ByteArrayInputStream(deser);
            ObjectInputStream oo = new ObjectInputStream(bo);
            return (CloudData) oo.readObject();
        } catch (Exception e) {
            Log.e("Deserialization Error", e.toString());
            if (data != null){
                Log.e("Data", data);
            }

            return new CloudData();
        }
    }
}
