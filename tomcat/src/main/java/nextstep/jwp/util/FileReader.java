package nextstep.jwp.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileReader {

    public static String read(String uri) {
        try {
            final Path path = Paths.get(ClassLoader
                .getSystemResource("static" + uri)
                .toURI()
            );

            final List<String> contents = Files.readAllLines(path);

            StringBuilder result = new StringBuilder();
            for (String content : contents) {
                result.append(content + "\n");
            }

            return result.toString();
        } catch (IOException | URISyntaxException e) {
            return "";
        }
    }

}
