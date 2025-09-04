package org.apache.coyote;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceLoader {

    private static final String ROOT_PATH = "/";
    private static final String INDEX_PAGE = "static/index.html";

    public static byte[] get(final String uri) throws IOException {
        URL url = getUrl(uri);

        if (url == null) {
            return new byte[0];
        }

        final var targetFile = url.getFile();
        return Files.readAllBytes(new File(targetFile).toPath());
    }

    private static URL getUrl(final String uri) {
        if (uri.equals(ROOT_PATH)) {
            return ResourceLoader.class.getClassLoader().getResource(INDEX_PAGE);
        }
        return ResourceLoader.class.getClassLoader().getResource("static" + uri);
    }
}
