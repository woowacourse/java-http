package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("Response를 생성할 때 초기값을 설정한다")
    void createResponse() {
        // given & when
        HttpResponse response = new HttpResponse(HttpStatus.OK);

        // then
        byte[] expected = "HTTP/1.1 200 OK\r\nConnection: keep-alive\r\n\r\n"
                .getBytes(StandardCharsets.UTF_8);

        assertThat(response.getBytes()).isEqualTo(expected);
    }

    @Test
    @DisplayName("HttpResponse header를 추가한다")
    void addHeader() {
        HttpResponse response = new HttpResponse(HttpStatus.OK);

        response.addHeader("Content-Type", "text/html");

        String actual = response.getHeader("Content-Type");
        assertThat(actual).isEqualTo("text/html");
    }

}
