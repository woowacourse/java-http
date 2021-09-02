package nextstep.jwp.utils;

import java.nio.ByteBuffer;

public class BytesUtils {

    public static byte[] concat(byte[]... bytesArr) {
        int flatSize = calculateFlatSize(bytesArr);
        ByteBuffer byteBuffer = ByteBuffer.allocate(flatSize);
        for (byte[] bytes : bytesArr) {
            byteBuffer.put(bytes);
        }
        return byteBuffer.array();
    }

    private static int calculateFlatSize(byte[][] bytesArr) {
        int size = 0;
        for (byte[] bytes : bytesArr) {
            size += bytes.length;
        }
        return size;
    }
}
