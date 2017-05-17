package click.remotely.common;

import java.util.List;

/**
 * Created by michzio on 13/05/2017.
 */

public class Bytes {


    public static  Byte[] getObjectArray(List<Byte> bytesList) {

        Byte[] bytes = new Byte[bytesList.size()];

        for (int i = 0; i < bytesList.size(); i++) {
            bytes[i] = bytesList.get(i);
        }

        return bytes;
    }

    public static byte[] getPrimitiveArray(List<Byte> bytesList) {

        byte[] bytes = new byte[bytesList.size()];

        for (int i = 0; i < bytesList.size(); i++) {
            bytes[i] = bytesList.get(i);
        }

        return bytes;
    }

    public static byte[] toPrimitiveArray(Byte[] Bytes) {

        byte[] bytes = new byte[Bytes.length];

        for(int i=0; i<Bytes.length; i++) {
            bytes[i] = Bytes[i];
        }

        return bytes;
    }

    public static Byte[] toObjectArray(byte[] bytes) {

        Byte[] Bytes = new Byte[bytes.length];

        int i=0;
        for(byte b : bytes)
            Bytes[i++] = b;

        return Bytes;
    }

}
