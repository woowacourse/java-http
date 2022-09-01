package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class HTTP11StaticFile {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_URL = "/";
    private static final String SPACE_DELIMITER = " ";
    private static final int URL_INDEX = 1;
    private static final String PATH_FROM_RESOURCE = "static";
    private static final String DEFAULT_MEDIA_TYPE = "text/html";

    private final String url;
    private final String content;

    private HTTP11StaticFile(final String url, final String content) {
        this.url = url;
        this.content = content;
    }

    public static HTTP11StaticFile of(final InputStream inputStream) throws IOException, URISyntaxException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String url = parseUrl(bufferedReader);
        final String body = parseContent(url);
        return new HTTP11StaticFile(url, body);
    }

    private static String parseUrl(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        return firstLine.split(SPACE_DELIMITER)[URL_INDEX];
    }

    private static String parseContent(final String url) throws URISyntaxException, IOException {
        if (isDefaultUrl(url)) {
            return DEFAULT_RESPONSE_BODY;
        }
        final List<String> bodyLines = Files.readAllLines(getStaticFilePath(url));
        return String.join("\n", bodyLines) + "\n";
    }

    private static boolean isDefaultUrl(final String url) {
        return DEFAULT_URL.equals(url);
    }

    private static Path getStaticFilePath(final String url) throws URISyntaxException {
        String filePathString = PATH_FROM_RESOURCE + url;
        final URL fileUrl = HTTP11StaticFile.class.getClassLoader().getResource(filePathString);
        final URI uri = Objects.requireNonNull(fileUrl).toURI();
        return Paths.get(uri);
    }

    public String getContentType() throws IOException {
        if (isDefaultUrl(url)) {
            return DEFAULT_MEDIA_TYPE;
        }
        final Path path = new File(url).toPath();
        return Files.probeContentType(path);
    }

    public String getContent() {
        return content;
    }

    public long getByteLength() {
        return content.getBytes().length;
    }
}
