package common;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    private static final String STATIC_RESOURCE_PATH = "static";

    private FileReader() {
    }

    public static String readFile(String uri) throws IOException {
        URL resource = findResource(uri);
        Path path = Paths.get(resource.getFile());
        return new String(Files.readAllBytes(path));
    }

    private static URL findResource(String fileName) {
        URL resource = FileReader.class.getClassLoader()
                .getResource(STATIC_RESOURCE_PATH + fileName);
        if (resource == null) {
            fileName = fileName + ".html";
            resource = FileReader.class.getClassLoader()
                    .getResource(STATIC_RESOURCE_PATH + fileName);
        }
        if (resource == null) {
            return FileReader.class.getClassLoader()
                    .getResource(STATIC_RESOURCE_PATH + "/404.html");
        }
        return resource;
    }
}
