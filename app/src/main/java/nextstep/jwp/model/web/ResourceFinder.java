package nextstep.jwp.model.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceFinder {

    private static final String DEFAULT_PATH = "static";

    public static String resource(String uri) throws IOException {
        URL url = ResourceFinder.class.getClassLoader().getResource(DEFAULT_PATH + uri);
        Path path = new File(url.getPath()).toPath();

        return new String(Files.readAllBytes(path));
    }
}
