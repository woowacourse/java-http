package org.apache.coyote.http11;

import java.io.File;
import java.net.URL;

public class ViewResolver {

    private ViewResolver() {
    }

    public static File findViewFile(String path) {
        String resourcePath = findResourcePath(path);
        URL resource = ViewResolver.class.getClassLoader()
                .getResource(String.format("static/%s", resourcePath));

        if (resource != null) {
            return new File(resource.getFile());
        }
        return null;
    }

    private static String findResourcePath(String path) {
        if (path.contains(".")) {
            return path;
        }
        return path + ".html";
    }


}
