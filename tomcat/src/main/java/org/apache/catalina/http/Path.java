package org.apache.catalina.http;

import org.apache.catalina.http.vo.FileExtension;
import org.apache.coyote.http11.util.HttpRequestParser;

import java.util.Map;
import java.util.Optional;

public class Path {

    private final String value;
    private final Map<String, String> params;

    public Path(String value) {
        this.value = value;
        this.params = parseQueryPath(value);
    }

    public Optional<FileExtension> getFileExtension() {
        String fileExtension = "json"; // default
        final var dotIndex = value.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < value.length() - 1) {
            fileExtension = value.substring(dotIndex + 1).toLowerCase();
        }
        final var extension = FileExtension.of(fileExtension);
        if (extension == FileExtension.NONE) {
            return Optional.empty();
        }
        return Optional.of(extension);
    }

    public String getURI() {
        final var queryStartIndex = value.indexOf("?");
        if (queryStartIndex == -1) {
            return value;
        }
        return value.substring(0, queryStartIndex);
    }

    public Map<String, String> getQueryParams() {
        return params;
    }

    private Map<String, String> parseQueryPath(final String path) {
        final var queryString = parseQueryString(path);
        return HttpRequestParser.parseQueryString(queryString);
    }

    private String parseQueryString(final String path) {
        final var startIndex = path.indexOf("?");
        if (startIndex == -1) {
            return "";
        }
        return path.substring(startIndex + 1);
    }

    public String getValue() {
        return value;
    }
}
