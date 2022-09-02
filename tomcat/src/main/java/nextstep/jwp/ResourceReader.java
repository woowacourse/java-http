package nextstep.jwp;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceReader {

    private final Logger log = LoggerFactory.getLogger(ResourceReader.class);

    public String read(final String target) throws NotFoundException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + target);
        if (resource == null) {
            throw new NotFoundException("자원을 찾지 못했음 : " + target);
        }
        return readFile(new File(resource.toURI()));
    }

    private String readFile(final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
