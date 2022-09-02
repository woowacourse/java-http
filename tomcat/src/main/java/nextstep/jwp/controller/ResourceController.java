package nextstep.jwp.controller;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceController implements Controller {

    final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Override
    public ResponseEntity execute(final String target) throws NotFoundException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + target);
        if (resource == null) {
            throw new NotFoundException("자원을 찾지 못했음 : " + target);
        }
        final String content = readFile(new File(resource.toURI()));
        final String contentType = getContentType(target);
        return new ResponseEntity(HttpStatus.OK, contentType, content);
    }

    private String readFile(final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private String getContentType(final String target) {
        if (target.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
