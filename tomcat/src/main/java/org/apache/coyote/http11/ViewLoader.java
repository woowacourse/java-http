package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ViewLoader {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private static final String STATIC_DIRECTORY = "static";

    private ViewLoader() {
    }

    public static String from(String viewName) {
        URL resource = classLoader.getResource(STATIC_DIRECTORY + viewName);
        final File file = new File(resource.getFile());
        try{
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String toIndex() {
        return from("/index.html");
    }

    public static String toNotFound() {
        return from("/404.html");
    }

    public static String toUnauthorized() {
        return from("/401.html");
    }
}
