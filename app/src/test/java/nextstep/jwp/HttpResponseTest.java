package nextstep.jwp;

import nextstep.jwp.ui.response.HttpResponse;
import nextstep.jwp.ui.response.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class HttpResponseTest {

    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        httpResponse = new HttpResponse();
    }

    @Test
    @DisplayName("등록한 상태코드대로 ResponseLine이 나온다.")
    void setStatus() {
        // give, when
        httpResponse.setStatus(HttpStatus.OK);

        // then
        assertThat(httpResponse.getResponseLine()).isEqualTo("HTTP/1.1 200 OK ");
    }

    @Test
    @DisplayName("Header를 추가한다.")
    void addHeader() {
        // given, when
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", "300");
        Map<String, String> headers = httpResponse.getHeaders();

        // then
        assertThat(headers).hasSize(2)
                .contains(entry("Content-Type", "text/html;charset=utf-8"),
                        entry("Content-Length", "300"));
    }

    @Test
    @DisplayName("등록한 상태코드, Header, Body대로 Response가 나온다.")
    void write() {
        // given, when
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", "300");
        httpResponse.write("ResponseBody");

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 300 ",
                "",
                "ResponseBody");
        assertThat(httpResponse.getResponse()).isEqualTo(expected);
    }

}
