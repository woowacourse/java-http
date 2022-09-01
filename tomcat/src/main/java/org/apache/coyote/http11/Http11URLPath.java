package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Http11URLPath {

    private static final String DEFAULT_MEDIA_TYPE = "text/html";
    private static final String SPACE_DELIMITER = " ";
    private static final String DEFAULT_URL = "/";
    private static final int URL_INDEX = 1;

    private final String url;

    private Http11URLPath(final String url) {
        this.url = url;
    }

    public static Http11URLPath of(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String path = parsePath(bufferedReader);
        return new Http11URLPath(path);
    }

    private static String parsePath(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final String parsedUrl = firstLine.split(SPACE_DELIMITER)[URL_INDEX];
        final String parsedPath = parsedUrl.split("\\?")[0];
        if (!parsedPath.contains(".")) {
            return parsedPath + ".html";
        }
        return parsedPath;
    }

    public boolean isDefault() {
        return DEFAULT_URL.equals(url);
    }

    public String getMediaType() throws IOException {
        if (this.isDefault()) {
            return DEFAULT_MEDIA_TYPE;
        }
        final Path path = new File(url).toPath();
        return Files.probeContentType(path);
    }

    public String value() {
        return url;
    }
}
