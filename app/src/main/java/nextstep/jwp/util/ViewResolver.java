package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViewResolver {

    private static final String HTML = "html";
    private static final String STATIC = "static";

    private final String requestedURL;

    public ViewResolver(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public String staticValue(String type) throws IOException {
        String url = requestedURL;
        if (type.equals(HTML) && !url.contains(HTML)) {
            url = url + "." + HTML;
            return loadFile(url);
        }
        return loadFile(url);
    }

    private String loadFile(String url) throws IOException {
        final String filePath = STATIC + url;
        final URL resource = getClass().getClassLoader().getResource(filePath);
        final Path path = new File(resource.getPath()).toPath();
        return Files.readString(path);
    }
}
