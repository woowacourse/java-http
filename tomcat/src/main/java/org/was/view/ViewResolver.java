package org.was.view;

import static org.apache.catalina.handler.StaticResourceHandler.RESOURCE_BASE_PATH;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ViewResolver {

    private static final ViewResolver INSTANCE =new ViewResolver();

    private ViewResolver() {
    }

    public static ViewResolver getInstance() {
        return INSTANCE;
    }

    public View getView(String viewName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(RESOURCE_BASE_PATH + viewName);
        if (resource == null) {
            return null;
        }

        File file = new File(resource.getFile());
        return new View(new String(Files.readAllBytes(file.toPath())));
    }
}
