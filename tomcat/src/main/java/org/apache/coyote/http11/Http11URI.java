package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Http11URI {

    private static final String DEFAULT_MEDIA_TYPE = "text/html";
    private static final String SPACE_DELIMITER = " ";
    private static final String DEFAULT_URL = "/";
    private static final int URL_INDEX = 1;
    private static final String FILE_EXTENSION_DELIMITER = ".";
    private static final String QUERY_PARAM_DELIMITER = "?";
    private static final String QUERY_PARAM_DELIMITER_REGEX = "\\?";
    private static final int PARAM_INDEX = 1;
    private static final int PATH_INDEX = 0;
    private static final String DEFAULT_FILE_EXTENSION = ".html";

    private final String path;
    private final Http11QueryParams params;

    private Http11URI(final String path, final Http11QueryParams params) {
        this.path = path;
        this.params = params;
    }

    public static Http11URI of(final InputStream inputStream) throws IOException {
        final String parsedUrl = parseUrl(inputStream);
        final Http11QueryParams params = parseQueryParams(parsedUrl);
        final String path = parsePath(parsedUrl);
        return new Http11URI(path, params);
    }

    private static String parseUrl(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String firstLine = bufferedReader.readLine();
        return firstLine.split(SPACE_DELIMITER)[URL_INDEX];
    }

    private static Http11QueryParams parseQueryParams(final String parsedUrl) {
        if (!parsedUrl.contains(QUERY_PARAM_DELIMITER)) {
            return Http11QueryParams.ofEmpty();
        }
        final String urlQueryParams = parsedUrl.split(QUERY_PARAM_DELIMITER_REGEX)[PARAM_INDEX];
        return Http11QueryParams.of(urlQueryParams);
    }

    private static String parsePath(final String parsedUrl) {
        final String parsedPath = parsedUrl.split(QUERY_PARAM_DELIMITER_REGEX)[PATH_INDEX];
        if (hasNotFileExtension(parsedPath) && !parsedPath.equals(DEFAULT_URL)) {
            return parsedPath + DEFAULT_FILE_EXTENSION;
        }
        return parsedPath;
    }

    private static boolean hasNotFileExtension(final String parsedPath) {
        return !parsedPath.contains(FILE_EXTENSION_DELIMITER);
    }

    public boolean isDefault() {
        return DEFAULT_URL.equals(path);
    }

    public String getMediaType() throws IOException {
        if (this.isDefault()) {
            return DEFAULT_MEDIA_TYPE;
        }
        final Path path = new File(this.path).toPath();
        return Files.probeContentType(path);
    }

    public String getPath() {
        return path;
    }

    public boolean hasPath(final String path) {
        return this.path.equals(path);
    }

    public String findParamByKey(final String key) {
        return params.get(key);
    }

    public boolean hasParams() {
        return Objects.nonNull(params);
    }
}
