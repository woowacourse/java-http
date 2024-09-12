package org.apache.catalina;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceResolver {

    public String resolve(String path) {
        if (path == null) {
            return null;
        }
        int extension = path.lastIndexOf(".");
        URL url = getClass().getClassLoader().getResource("static" + path);
        if (extension == -1) {
            url = getClass().getClassLoader().getResource("static" + path + ".html");
        }
        return readFile(url);
    }

    private String readFile(URL url) {
        if (url == null) {
            return "Hello world!";
        }
        File file = new File(url.getFile());
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }
}
