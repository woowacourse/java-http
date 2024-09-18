package org.apache.coyote.http11.request.requestline;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpLocation {
    public static final String DEFAULT_EXTENSION = "html";
    private final String fileName;
    private final String extension;

    private static final Pattern validPattern = Pattern.compile("^[A-Za-z0-9-_~./]+$");

    public HttpLocation(String data) {
        if (data == null || data.codePoints().filter(r -> r == '.').count() > 1) {
            throw new IllegalArgumentException("location's dot must less than one");
        }
        List<String> split = Arrays.stream(data.split("\\."))
                .collect(Collectors.toList());

        validate(split);

        fileName = split.getFirst();
        extension = split.size() == 1 ? DEFAULT_EXTENSION : split.get(1);
    }

    public static HttpLocation from(String data) {
        return new HttpLocation(data);
    }

    private void validate(List<String> split) {
        if (split.isEmpty()) {
            throw new IllegalArgumentException("location empty parse error");
        }
        if (!split.getFirst().startsWith("/")) {
            throw new IllegalArgumentException("location first character parse error");
        }
        if (split.size() > 2) {
            throw new IllegalArgumentException("location length parse error");
        }

        if (split.stream().anyMatch(r -> !validPattern.matcher(r).matches())) {
            throw new IllegalArgumentException("location character parse error");
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }
}
