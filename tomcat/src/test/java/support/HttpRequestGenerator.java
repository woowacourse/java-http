package support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpRequestGenerator {
    
    private HttpRequestGenerator() {
    }

    public static HttpRequest generate(final String requestString) throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return HttpRequest.from(reader);
    }
}
