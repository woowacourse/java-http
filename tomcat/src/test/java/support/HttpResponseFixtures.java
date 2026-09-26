package support;

import org.apache.coyote.http11.HttpResponse;

import java.nio.charset.StandardCharsets;

public final class HttpResponseFixtures {

    private HttpResponseFixtures() {
    }

    public static String responseText(HttpResponse response) {
        return new String(response.toBytes(), StandardCharsets.UTF_8);
    }
}
