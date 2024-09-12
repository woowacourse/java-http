package util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceFileLoader {

    public static String loadStaticFileToString(String filePath) throws IOException {
        URL resource = ResourceFileLoader.class.getClassLoader().getResource("static" + filePath);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
