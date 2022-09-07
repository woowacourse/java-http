package nextstep.jwp.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void writeResponse() {
        HttpResponse httpResponse = HttpResponse.of(StatusCode.OK, ContentType.TEXT_PLAIN, "Hello World!");

        String actual = new String(httpResponse.writeResponse());

        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
             "Content-Type: text/plain;charset=utf-8 ",
             "Content-Length: 12 ",
             "",
             "Hello World!");
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
