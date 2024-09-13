package org.apache.coyote.http11.resource;

import java.io.File;
import java.net.URL;
import org.apache.coyote.http11.HttpRequest;

public class ResourceParser {

    public static File getRequestFile(String filePath) {
        try {
            return getFile(filePath);
        } catch (NullPointerException exception) {
            return getFile("/404.html");
        }
    }

    private static File getFile(String filePath) throws NullPointerException {
        String resourcePath = "static" + filePath;
        URL resource = ResourceParser.class.getClassLoader().getResource(resourcePath);

        return new File(resource.getPath());
    }
}
