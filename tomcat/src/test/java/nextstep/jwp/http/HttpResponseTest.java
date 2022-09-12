package nextstep.jwp.http;

import org.apache.http.ContentType;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
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
