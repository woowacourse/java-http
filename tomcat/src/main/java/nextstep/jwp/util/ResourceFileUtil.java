package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceFileUtil {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public static String readAll(String path) {
        try {
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
