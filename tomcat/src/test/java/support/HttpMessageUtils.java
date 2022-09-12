package support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.request.HttpRequest;

public class HttpMessageUtils {

    public static String extractSetCookieSessionId(String socketOutput) {
        final var right = socketOutput.split("Set-Cookie: JSESSIONID=")[1];
        return right.split("; Max-Age=600")[0];
    }

    public static HttpRequest toRequest(String httpRequest) throws IOException {
        try (final var inputStream = new ByteArrayInputStream(httpRequest.getBytes());
             final var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final var bufferedReader = new BufferedReader(reader)) {
            return HttpRequest.of(bufferedReader);
        }
    }
}
