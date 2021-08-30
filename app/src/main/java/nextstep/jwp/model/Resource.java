package nextstep.jwp.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class Resource {

    private final String path;

    public Resource(String uri) {
        this.path = "static" + uri;
    }

    public String getContent() throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IOException("resource 값이 null 입니다.");
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
