package org.apache.catalina.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseParserTest {

    @DisplayName("HttpResponse을 String으로 변환한다.")
    @Test
    void parse() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusLine(Status.OK);
        httpResponse.setBody("Hello world!");

        String actual = new ResponseParser().parse(httpResponse);

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
