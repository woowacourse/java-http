package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @Test
    void ok() {
        final HttpResponse httpResponse = HttpResponse.ok("/index.html", "body");
        final String message = httpResponse.getMessage();

        assertThat(message).contains("200 OK");
    }

    @Test
    void found() {
        final HttpResponse httpResponse = HttpResponse.found("/index.html", "body");
        final String message = httpResponse.getMessage();

        assertThat(message).contains("302 FOUND");
    }

    @Test
    void unauthorized() {
        final HttpResponse httpResponse = HttpResponse.unauthorized();
        final String message = httpResponse.getMessage();

        assertThat(message).contains("401 UNAUTHORIZED");
    }

    @Test
    void notFound() {
        final HttpResponse httpResponse = HttpResponse.notFound();
        final String message = httpResponse.getMessage();

        assertThat(message).contains("404 NOT FOUND");
    }

    @Test
    void internalServerError() {
        final HttpResponse httpResponse = HttpResponse.internalServerError();
        final String message = httpResponse.getMessage();

        assertThat(message).contains("500 INTERNAL SERVER ERROR");
    }
}
