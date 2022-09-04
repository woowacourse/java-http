package nextstep.jwp.support;

import javassist.NotFoundException;
import nextstep.jwp.exception.FileAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class Resource {

    private final Logger log = LoggerFactory.getLogger(Resource.class);

    private final String target;

    public Resource(final String target) {
        this.target = target;
    }

    public String read() throws NotFoundException {
        final URL resource = getClass().getClassLoader().getResource("static" + target);
        if (Objects.isNull(resource)) {
            throw new NotFoundException("자원을 찾지 못했음 : " + target);
        }
        return read(new File(getUri(resource)));
    }

    private String read(final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String getContentType() throws NotFoundException {
        final URL resource = getClass().getClassLoader().getResource("static" + target);
        if (Objects.isNull(resource)) {
            throw new NotFoundException("자원을 찾지 못했음 : " + target);
        }
        final File file = new File(getUri(resource));
        return findOutContentType(file);
    }

    private String findOutContentType(final File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new FileAccessException();
        }
    }

    private URI getUri(final URL resource) {
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new FileAccessException();
        }
    }
}
