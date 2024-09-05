package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    String file(String url) throws URISyntaxException, IOException {
        URL resource = getUrl(url);
        String path = resource.getPath();
        String[] strings = path.split("\\.");
        String extension = strings[strings.length - 1];
        String file = new String(Files.readAllBytes(Path.of(resource.toURI())));
        return file;
    }

    private URL getUrl(String path) {
        path = "static" + path;
        return getClass().getClassLoader().getResource(path);
    }

}
