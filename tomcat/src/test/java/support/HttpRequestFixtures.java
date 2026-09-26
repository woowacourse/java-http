package support;

import org.apache.coyote.http11.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class HttpRequestFixtures {

    private HttpRequestFixtures() {
    }

    public static HttpRequest httpRequest(String rawRequest) {
        try {
            return HttpRequest.from(new ByteArrayInputStream(
                    rawRequest.getBytes(StandardCharsets.UTF_8)
            ));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
