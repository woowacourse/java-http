package nextstep.jwp.model;

import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    public static final String HTTP_FORWARD_TXT = "./src/test/resources/http_forward.txt";
    public static final String HTTP_REDIRECT_TXT = "./src/test/resources/http_redirect.txt";

    @Test
    void responseForward() throws IOException {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        response.forward("/index.html");

        // then
        String body = FileUtils.readFileOfUrl("/index.html");
        String expectedHeader = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                body);
        assertThat(outputStream.toString()).isEqualTo(expectedHeader);
    }

    @Test
    void responseRedirect() throws IOException {

        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        response.redirect("/401.html");

        // then
        String expectedHeader = String.join("\r\n",
                "HTTP/1.1 302 Redirect ",
                "Location: /401.html ",
                "");
        assertThat(outputStream.toString()).hasToString(expectedHeader);
    }
}