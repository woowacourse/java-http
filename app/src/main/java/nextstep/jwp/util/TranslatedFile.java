package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class TranslatedFile {

    private final String requestedURL;

    public TranslatedFile(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public String staticValue(String type) throws IOException {
        String url = requestedURL;
        if (type.equals("html") && !url.contains("html")) {
            url = url + ".html";
            return loadFile(url);
        }
        return loadFile(url);
    }

    private String loadFile(String url) throws IOException {
        final String filePath = "static" + url;
        final URL resource = getClass().getClassLoader().getResource(filePath);
        final Path path = new File(resource.getPath()).toPath();
        return Files.readString(path);
    }
}
