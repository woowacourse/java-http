package org.apache.catalina;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.exception.FileNotReadableException;

public class FileReader {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    private FileReader() {
    }

    public static String readStaticFile(final String fileName) throws FileNotReadableException {
        try {
            return new String(Files.readAllBytes(new File(getFilePath(fileName).getFile()).toPath()));
        } catch (IOException e) {
            throw new FileNotReadableException();
        }
    }

    private static URL getFilePath(final String path){
        final URL url = classLoader.getResource("static" + path);
        if (url == null) {
            return classLoader.getResource("static/404.html");
        }
        return url;
    }
}
