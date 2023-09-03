package util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceFileReader {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public static String readFile(final String path) throws IOException {

        URL resource = classLoader.getResource("static"+path);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
