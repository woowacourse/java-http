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
        String fileName = "404.html";
        if (requestURIPath.equals("/")) {
            fileName = "index.html";
        }
        if (requestURIPath.length() > 2) {
            fileName = requestURIPath.substring(1, requestURIPath.length());
        }
        return read(fileName);
    }

    public String read(String fileName) {
        String filePath = "static/" + fileName;
        URL resource = getClass().getClassLoader().getResource(filePath);
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
