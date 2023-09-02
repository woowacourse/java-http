package org.apache.coyote.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ByteUtil {

    private static final Logger log = LoggerFactory.getLogger(ByteUtil.class);

    private ByteUtil() {

    }

    public static int readStreamUntilEndsWith(InputStream inputStream, byte[] source, byte[] target) {
        int sourceLength = 0;
        int capacity = source.length;

        try {
            while (!endsWith(source, sourceLength, target) && sourceLength < capacity) {
                int readBytes = inputStream.read(source, sourceLength++, 1);
                if (readBytes == -1) {
                    break;
                }
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            log.error(e.getMessage(), e);
        }
        return sourceLength;
    }

    public static boolean endsWith(byte[] source, int sourceLength, byte[] target) {
        int targetLength = target.length;
        if (sourceLength < targetLength) {
            return false;
        }

        for (int targetIndex = 0; targetIndex < targetLength; targetIndex++) {
            int sourceIndex = sourceLength - targetLength + targetIndex;
            if (source[sourceIndex] != target[targetIndex]) {
                return false;
            }
        }
        return true;
    }

    public static void readStreamOfLength(InputStream inputStream, byte[] source, int contentLength) {
        try {
            inputStream.read(source, 0, contentLength);
        } catch (IOException | IndexOutOfBoundsException e) {
            log.error(e.getMessage(), e);
        }
    }
}
