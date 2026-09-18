package org.qupring.file;

import java.io.FileReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class HtmlReader {

    public static String read(String fileName) {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(fileName);

        if (Objects.isNull(url)) {
            return "";
        }

        final Path path = Path.of(url.getPath());

        StringBuilder sb = new StringBuilder();
        try (FileReader fileReader = new FileReader(path.toFile())) {
            int ch;
            while ((ch = fileReader.read()) != -1) {
                sb.append((char) ch);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

}
