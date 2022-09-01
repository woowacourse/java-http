package org.apache.coyote.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.exception.NotFoundException;

public class ResourceResponse {

    private final String content;

    public ResourceResponse(String uri) {
        this.content = findResource(uri);
    }

    private String findResource(String uri) {
        try {
            File file = new File(findUrl(uri).getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (NullPointerException e) {
            throw new NotFoundException();
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private URL findUrl(String uri) {
        final var classLoader = getClass().getClassLoader();
        if (uri.equals("/")) {
            return classLoader.getResource("static/index.html");
        }
        return classLoader.getResource("static" + uri);
    }

    public String toContent() {
        return content;
    }
}
