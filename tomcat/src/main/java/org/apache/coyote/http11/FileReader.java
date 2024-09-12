package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    private static final String SUFFIX_OF_STATIC = "static";

    private FileReader() {
    }

    public static String read(String fileName) throws IOException {
        URL resource = FileReader.class.getClassLoader().getResource(SUFFIX_OF_STATIC + fileName);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public static String probeContentType(String location) throws IOException {
        return Files.probeContentType(Path.of(SUFFIX_OF_STATIC + location));
    }
}
