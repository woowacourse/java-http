package nextstep.jwp.model.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class DefaultHttpHandler implements CustomHandler {

    protected String findResourceFile(String uri) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(uri);
        final Path path = Paths.get(resource.toURI());
        return new String(Files.readAllBytes(path));
    }
}
