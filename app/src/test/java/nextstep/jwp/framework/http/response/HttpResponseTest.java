package nextstep.jwp.framework.http.response;

import nextstep.jwp.framework.http.response.details.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


class HttpResponseTest {

    @DisplayName("파일과 ResponseStatus를 넘겨주면 HttpResponse를 생성한다.")
    @Test
    void generateWithFileAndStatus() {
        //given
        final String fileName = "static/test.html";
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

        //when
        final HttpResponse httpResponse = HttpResponse.of(file, ResponseStatus.OK);

        //then
        final String response = httpResponse.generateResponse();
        final String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 134\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "\r\n" +
                "<!DOCTYPE html>\r\n" +
                "<html lang=\"en\">\r\n" +
                "<head>\r\n" +
                "    <meta charset=\"UTF-8\">\r\n" +
                "    <title>Title</title>\r\n" +
                "</head>\r\n" +
                "<body>\r\n" +
                "\r\n" +
                "</body>\r\n" +
                "</html>\r\n";
        assertThat(response).isEqualTo(expected);
    }
}