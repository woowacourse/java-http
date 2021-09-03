package nextstep.jwp.http.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ViewResolver {

    private final String uri;

    public ViewResolver(String uri) {
        this.uri = uri;
    }

    public String getContent() throws IOException {
        URL resource = getResource();
        if (Objects.isNull(resource)) {
            return null;
        }
        Path path = new File(resource.getPath()).toPath();
        return Files.readString(path);
    }

    private URL getResource() {
        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (Objects.isNull(resource)) {
            resource = getClass().getClassLoader().getResource("static/" + uri + ".html");
        }
        return resource;
    }
}
