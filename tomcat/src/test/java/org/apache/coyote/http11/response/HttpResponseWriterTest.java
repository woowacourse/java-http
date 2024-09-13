package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;
import org.junit.jupiter.api.Test;

class HttpResponseWriterTest {

    @Test
    void HttpResponse_응답_출력을_위한_변환() {
        // given
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46" );
        headers.put("Content-Type", "text/html" );
        headers.put("Content-Length", "11" );
        ResponseHeader responseHeader = new ResponseHeader(headers);
        ResponseBody responseBody = new ResponseBody("hello world" );

        HttpResponse response = new HttpResponse();
        response.setResponse(HttpStatusCode.FOUND, responseHeader, responseBody);

        HttpResponseWriter responseWriter = HttpResponseWriter.getInstance();

        // when
        byte[] actual = responseWriter.write(response);
        String actualResponse = new String(actual, StandardCharsets.UTF_8);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "Content-Type: text/html " ,
                "Content-Length: 11 ",
                "",
                "hello world" );

        assertThat(actualResponse).isEqualTo(expected);
    }
}
