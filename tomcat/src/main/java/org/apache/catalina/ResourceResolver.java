package org.apache.catalina;

import java.net.URL;

public class ResourceResolver {

    public String resolve(String path) {
        int extension = path.lastIndexOf(".");
        if (extension == -1) {
            URL url = getClass().getClassLoader().getResource("static" + path + ".html");
            return getPath(url);
        }
        URL url = getClass().getClassLoader().getResource("static" + path);
        return getPath(url);
    }

    private String getPath(URL url) {
        if (url == null) {
            return "Hello world!";
        }
        return url.getPath();
    }
}
