package hook;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class KeyHook {
    static byte[] key;
    static File keyFile = new File("/sdcard/qkey");

    static {
        try {
            FileInputStream fis = new FileInputStream(keyFile);

            if(fis.available() == 16){
                key = new byte[16];
                fis.read(key);
            }

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("KeyHook", "file not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("KeyHook", "IOException: " + e.getMessage());
        }
    }

    public static void updateKey(byte[] key){
        if(key == null) return;
        if(KeyHook.key != null){
            if(KeyHook.key.length == key.length){
                boolean equal = true;
                for(int i = 0; i < key.length; i++){
                    if(KeyHook.key[i] != key[i]){
                        equal = false;
                    }
                }

                if(equal) return;
            }
        }

        KeyHook.key = key;

        String keyString = "0x" + bytesToHex(key).replace(" ", "");

        Log.d("KeyHook", "new key: "+ keyString);

        try {
            if(!keyFile.exists()) keyFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(keyFile);
            fos.write(keyString.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("KeyHook", "file not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("KeyHook", "IOException: " + e.getMessage());
        }

        Log.d("KeyHook", "key file updated");
    }

    static String bytesToHex(byte[] bytes) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        String hex = "";
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hex += HEX_ARRAY[v >>> 4];
            hex += HEX_ARRAY[v & 0x0F];
            hex += " ";
        }
        return hex;
    }
}
