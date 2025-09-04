package org.apache.coyote.http11.http.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class HttpResponseBody {

    private final String value;

    private HttpResponseBody(final String value) {
        final URL url = getClass().getClassLoader().getResource("static/" + value);

        if (url == null) {
            this.value = value;
            return;
        }

        try {
            final Path path = Paths.get(url.toURI());
            this.value = new String(Files.readAllBytes(path));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("response 구성 중 문제가 발생하였습니다", e);
        }
    }

    public static HttpResponseBody emptyBody() {
        final String emptyBody = null;
        return new HttpResponseBody(emptyBody);
    }

    public static HttpResponseBody withString(String value) {
        validateNull(value);
        return new HttpResponseBody(value);
    }

    private static void validateNull(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("문자열은 null일 수 없습니다");
        }
    }

    public Optional<String> getValue() {
        return Optional.of(value);
    }

    public int getByteLength() {
        if (value == null) {
            throw new IllegalArgumentException("body 내용물이 존재하지 않습니다");
        }
        return value.getBytes().length;
    }
}
