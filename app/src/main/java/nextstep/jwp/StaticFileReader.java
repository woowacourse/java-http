package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.http.HttpRequest;

public class StaticFileReader {

    public String read(HttpRequest httpRequest) {
        String requestURIPath = httpRequest.extractURIPath();
        String fileName = "static" + requestURIPath;
        return read(fileName);
    }

    public String read(String fileName) {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (Objects.isNull(resource)) {
            return null;
        }
        Path path = new File(resource.getFile()).toPath();
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
