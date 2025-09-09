package com.techcourse.servlet.util;

import com.techcourse.exception.NoResourceFoundException;
import java.io.IOException;
import java.io.InputStream;

public class StaticFileLoader {

    public static byte[] loadStaticFile(String path) throws IOException {
        try (InputStream inputStream = StaticFileLoader.class.getClassLoader().getResourceAsStream(path)) {
            validateResourceExists(path, inputStream);
            return inputStream.readAllBytes();
        }
    }

    private static void validateResourceExists(String path, InputStream inputStream) {
        if (inputStream == null) {
            throw new NoResourceFoundException(path);
        }
    }
}
