package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileReader {

    private FileReader() {
    }

    public static String read(String fileName) throws IOException {
        URL resource = FileReader.class.getClassLoader().getResource(fileName);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
