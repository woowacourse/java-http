package org.apache.coyote.http11.response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {
    @DisplayName("HttpResponse를 string으로 생성한다.")
    @Test
    void toStringResponse() throws IOException {
        // given
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setCookie("1234567890");
        httpResponse.setContentType(MimeType.NONE);
        httpResponse.setLocation("location");
        httpResponse.setBody(new ResponseBody("hello"));

        // when
        String result = httpResponse.serialize();

        // then
        String expectedResponseLine = "HTTP/1.1 200 OK \r\n";
        String expectedContentType = "Content-Type: application/octet-stream \r\n";
        String expectedContentLength = "Content-Length: 5 \r\n";
        String expectedCookie = "Set-Cookie: 1234567890 \r\n";
        String expectedLocation = "Location: location \r\n";
        String expectedResponseBody = "hello";

        assertThat(result).contains(
                expectedResponseLine,
                expectedContentType,
                expectedContentLength,
                expectedCookie,
                expectedLocation,
                expectedResponseBody
        );
    }
}
