package org.apache.coyote.http11.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileReader {

    private FileReader() {

    }

    public static String readFile(String endPoint) throws IOException {
        ClassLoader classLoader = FileReader.class.getClassLoader();
        URL resource = classLoader.getResource("static" + endPoint);

        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
