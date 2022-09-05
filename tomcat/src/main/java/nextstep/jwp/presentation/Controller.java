package nextstep.jwp.presentation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.exception.FileNotFoundException;

public abstract class Controller {

    private static final String RESOURCE_PATH = "static";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String EXTENSION_DELIMITER = ".";

    protected String getContent(final String path) throws FileNotFoundException, IOException {
        final URL resource = getClass()
                .getClassLoader()
                .getResource(RESOURCE_PATH + path + getExtension(path));
        if (resource == null) {
            throw new FileNotFoundException();
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getExtension(final String path) {
        if (path.contains(EXTENSION_DELIMITER)) {
            return "";
        }
        return EXTENSION_DELIMITER + DEFAULT_EXTENSION;
    }

    public abstract ResponseEntity run(final String startLin) throws IOException;
}
