package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.exception.FileNotFoundException;

public class ResponseEntity {

    private static final String RESOURCE_PATH = "static";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String EXTENSION_DELIMITER = ".";

    private final StatusCode statusCode;
    private final String path;
    private String body;

    public ResponseEntity(final StatusCode statusCode, final String path) {
        this.statusCode = statusCode;
        this.path = path;
    }

    public ResponseEntity body(String body) {
        this.body = body;
        return this;
    }

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

    public String getResponse() throws IOException {
        final ResponseHeader responseHeader = new ResponseHeader(path);
        if (this.body == null) {
            this.body = getContent(path);
        }
        return String.join("\r\n", responseHeader.getHeader(statusCode, body), body);
    }
}
