package org.apache.catalina.core;

import java.io.IOException;
import java.io.InputStream;

public class StaticResourceHandler {

    public static byte[] readResource(String path) {
        try (InputStream inputStream = StaticResourceHandler.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }
}
