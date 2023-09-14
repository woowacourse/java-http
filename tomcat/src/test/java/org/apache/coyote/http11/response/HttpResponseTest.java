package org.apache.coyote.http11.response;

import static org.apache.coyote.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void create() {
        //given
        final String body = "<html></html>";
        final List<String> headers = List.of(
            "Content-Type: text/html",
            "Content-Length: " + body.getBytes().length
        );

        //when
        final HttpResponse response = new HttpResponse(StatusCode.OK, headers, body);

        //then

        final Map<String, String> expectedHeaders = Map.of(
            "Content-Type", "text/html",
            "Content-Length", String.valueOf(body.getBytes().length)
        );
        assertAll(
            () -> assertThat(response.getHttpVersion()).isEqualTo(HTTP_1_1),
            () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK),
            () -> assertThat(response.getStatusMessage()).isEqualTo("OK"),
            () -> assertThat(response.getHeaders()).isEqualTo(expectedHeaders),
            () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @Test
    void ok() {
        //given
        //when
        final HttpResponse response = HttpResponse.ok();

        //then
        assertAll(
            () -> assertThat(response.getHttpVersion()).isEqualTo(HTTP_1_1),
            () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK),
            () -> assertThat(response.getStatusMessage()).isEqualTo("OK"),
            () -> assertThat(response.getHeaders()).isEmpty(),
            () -> assertThat(response.getBody()).isEmpty()
        );
    }

    @Test
    void ok_withBody() {
        //given
        final ContentType contentType = ContentType.HTML;
        final String body = "<html></html>";

        //when
        final HttpResponse response = HttpResponse.ok(contentType, body);

        //then
        assertAll(
            () -> assertThat(response.getHttpVersion()).isEqualTo(HTTP_1_1),
            () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK),
            () -> assertThat(response.getStatusMessage()).isEqualTo("OK"),
            () -> assertThat(response.getHeaders()).isEqualTo(
                Map.of(
                    "Content-Type", "text/html",
                    "Content-Length", String.valueOf(body.getBytes().length)
                )
            ),
            () -> assertThat(response.getBody()).isEqualTo("<html></html>")
        );
    }

    @Test
    void simpleBody() {
        //given
        final ContentType contentType = ContentType.HTML;
        final String body = "<html></html>";

        //when
        final HttpResponse response = HttpResponse.simpleBody(StatusCode.OK, contentType, body);

        //then
        assertAll(
            () -> assertThat(response.getHttpVersion()).isEqualTo(HTTP_1_1),
            () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK),
            () -> assertThat(response.getStatusMessage()).isEqualTo("OK"),
            () -> assertThat(response.getHeaders()).isEqualTo(
                Map.of(
                    "Content-Type", "text/html",
                    "Content-Length", String.valueOf(body.getBytes().length))
            ),
            () -> assertThat(response.getBody()).isEqualTo("<html></html>")
        );
    }

    @Test
    void notFound() {
        //given
        //when
        final HttpResponse response = HttpResponse.notFound();

        //then
        assertAll(
            () -> assertThat(response.getHttpVersion()).isEqualTo(HTTP_1_1),
            () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.NOT_FOUND),
            () -> assertThat(response.getStatusMessage()).isEqualTo("Not Found"),
            () -> assertThat(response.getHeaders()).isEmpty(),
            () -> assertThat(response.getBody()).isEmpty()
        );
    }

    @Test
    void redirect() {
        //given
        //when
        final HttpResponse response = HttpResponse.redirect("/home");

        //then
        assertAll(
            () -> assertThat(response.getHttpVersion()).isEqualTo(HTTP_1_1),
            () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.FOUND),
            () -> assertThat(response.getStatusMessage()).isEqualTo("Found"),
            () -> assertThat(response.getHeaders()).isEqualTo(Map.of("Location", "/home")),
            () -> assertThat(response.getBody()).isEmpty()
        );
    }

    @Test
    void convertToMessage() {
        //given
        final ContentType contentType = ContentType.HTML;
        final String body = "<html></html>";
        final HttpResponse response = HttpResponse.ok(contentType, body);

        //when
        final String responseMessage = response.convertToMessage();

        //then
        final String expect = "HTTP/1.1 200 OK \n"
            + "Content-Type: text/html \n"
            + "Content-Length: 13 \n"
            + "\n"
            + "<html></html>";

        assertThat(responseMessage).isEqualTo(expect);
    }

    @Test
    void addCookie() {
        //given
        final HttpResponse response = HttpResponse.ok();

        //when
        response.addCookie("name", "split");

        //then
        assertThat(response.getCookie().getValues()).isEqualTo(Map.of("name", "split"));
    }
}
