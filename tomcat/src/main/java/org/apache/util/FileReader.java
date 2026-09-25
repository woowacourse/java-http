package org.apache.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileReader {
    public String readFile(String path) throws IOException {
        final URL url = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(url.getFile()).toPath()), StandardCharsets.UTF_8);
    }
}
