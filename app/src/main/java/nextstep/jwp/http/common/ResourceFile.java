package nextstep.jwp.http.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResourceFile {

    private static final String STATIC = "static";
    private final String path;

    public ResourceFile(String path) {
        this.path = STATIC + path;
    }

    public String getContent() throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        final Path path = new File(resource.getPath()).toPath();
        final List<String> actual = Files.readAllLines(path);
        return String.join("\r\n", actual);
    }

    public String getContentType() {
        return ContentType.findContentType(path);
    }
}
