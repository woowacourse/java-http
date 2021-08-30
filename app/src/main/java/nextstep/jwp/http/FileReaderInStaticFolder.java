package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileReaderInStaticFolder {

    private static final String INDEX_PAGE = "index.html";

    public String read(HttpRequest httpRequest) {
        String requestURIPath = httpRequest.extractURIPath();
        String fileName = null;
        if (requestURIPath.equals("/")) {
            fileName = INDEX_PAGE;
        }
        if (requestURIPath.length() > 2) {
            fileName = requestURIPath.substring(1);
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
            throw new CustomException("IOException");
        }
    }
}
