package nextstep.jwp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class HttpResponseTest {

    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        httpResponse = new HttpResponse(new ByteArrayOutputStream());
    }

    @Test
    void setStatus() {
        // give, when
        httpResponse.setStatus(200);

        // then
        assertThat(httpResponse.getResponseLine()).isEqualTo("HTTP/1.1 200 OK ");
    }

    @Test
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
    void write() throws IOException {
        // given, when
        httpResponse.setStatus(200);
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
