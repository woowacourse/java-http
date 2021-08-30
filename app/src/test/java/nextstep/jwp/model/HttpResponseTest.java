package nextstep.jwp.model;

import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @DisplayName("인덱스 페이지 조회에 대한 응답을 확인한다.")
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

    @DisplayName("401 페이지 리다이렉트에 대한 응답을 확인한다.")
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