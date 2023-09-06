package org.apache.coyote.http.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpBody {

    private static final HttpBody EMPTY = new HttpBody("");

    private final String value;

    public HttpBody(final String value) {
        this.value = value;
    }

    public static HttpBody file(final String filePath) throws IOException {
        final URL fileUrl = HttpBody.class.getClassLoader().getResource(filePath);
        final Path path = new File(fileUrl.getPath()).toPath();
        return new HttpBody(new String(Files.readAllBytes(path)));
    }

    public static HttpBody empty() {
        return EMPTY;
    }

    public Map<String, String> parseBodyParameters() {
        if (value.isBlank()) {
            throw new IllegalStateException("Empty body 는 key-value 로 parsing 할 수 없습니다.");
        }

        return Arrays.stream(value.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }

    public boolean isEmpty() {
        return value.isBlank();
    }

    public String getValue() {
        return value;
    }
}
