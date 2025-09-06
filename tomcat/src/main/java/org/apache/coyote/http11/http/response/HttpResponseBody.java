package org.apache.coyote.http11.http.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class HttpResponseBody {

    private final byte[] value;

    private HttpResponseBody(final byte[] value) {
        this.value = value;
    }

    public static HttpResponseBody emptyBody() {
        return new HttpResponseBody(new byte[0]);
    }

    public static HttpResponseBody withString(String value) {
        validateNull(value);
        return new HttpResponseBody(value.getBytes(StandardCharsets.UTF_8));
    }

    public static HttpResponseBody withStaticResourceName(String resourceName) {
        final URL url = HttpResponseBody.class.getClassLoader().getResource("static/" + resourceName);

        if (url == null) {
            return withString(resourceName);
        }

        try {
            final Path path = Paths.get(url.toURI());
            return new HttpResponseBody(Files.readAllBytes(path));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("response 구성 중 문제가 발생하였습니다", e);
        }
    }

    private static void validateNull(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("문자열은 null일 수 없습니다");
        }
    }

    public Optional<byte[]> getValue() {
        return Optional.ofNullable(value);
    }

    public int getByteLength() {
        if (value == null) {
            throw new IllegalArgumentException("body 내용물이 존재하지 않습니다");
        }
        return value.length;
    }
}
