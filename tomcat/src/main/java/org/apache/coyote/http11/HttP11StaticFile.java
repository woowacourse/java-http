package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class HttP11StaticFile {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_URL = "/";
    private static final String SPACE_DELIMITER = " ";
    private static final int URL_INDEX = 1;
    private static final String PATH_FROM_RESOURCE = "static";

    private final Http11URLPath http11URLPath;
    private final String content;

    private HttP11StaticFile(final Http11URLPath http11URLPath, final String content) {
        this.http11URLPath = http11URLPath;
        this.content = content;
    }

    public static HttP11StaticFile of(final InputStream inputStream) throws IOException, URISyntaxException {
        final Http11URLPath http11URLPath = Http11URLPath.of(inputStream);
        final String body = parseContent(http11URLPath);
        return new HttP11StaticFile(http11URLPath, body);
    }

    private static String parseContent(final Http11URLPath http11URLPath) throws IOException, URISyntaxException {
        if (http11URLPath.isDefault()) {
            return DEFAULT_RESPONSE_BODY;
        }

        final List<String> bodyLines = Files.readAllLines(getStaticFilePath(http11URLPath));
        return String.join("\n", bodyLines) + "\n";
    }

    private static Path getStaticFilePath(final Http11URLPath http11URLPath) throws URISyntaxException {
        String filePathString = PATH_FROM_RESOURCE + http11URLPath.value();
        final URL fileUrl = HttP11StaticFile.class.getClassLoader().getResource(filePathString);
        final URI uri = Objects.requireNonNull(fileUrl).toURI();
        return Paths.get(uri);
    }

    public String getContentType() throws IOException {
        return http11URLPath.getMediaType();
    }

    public String getContent() {
        return content;
    }

    public long getByteLength() {
        return content.getBytes().length;
    }
}
