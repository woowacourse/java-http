package org.apache.catalina.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ViewResolver {

    public static View getView(String viewName) throws IOException {
        ClassLoader classLoader = ViewResolver.class.getClassLoader();
        URL resource = classLoader.getResource("static" + viewName);
        if (resource == null) {
            return null;
        }

        File file = new File(resource.getFile());
        return new View(new String(Files.readAllBytes(file.toPath())));
    }
}
