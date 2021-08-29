package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class TranslatedFile {

    private String requestedURI;

    public TranslatedFile(String requestedURI) {
        this.requestedURI = requestedURI;
    }

    public String staticValue() throws IOException {
        final String filePath = "static" + requestedURI;
        final URL resource = getClass().getClassLoader().getResource(filePath);
        final Path path = new File(resource.getPath()).toPath();
        final String responseBody = Files.readString(path);
        return responseBody;
    }
}
