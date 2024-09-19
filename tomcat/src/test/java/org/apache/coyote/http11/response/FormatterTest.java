package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FormatterTest {

    @Test
    @DisplayName("HttpResponse를 바이트 출력 형식에 맞게 포맷팅한다.")
    void toResponseFormat() {
        //given
        String responseBody = "Hello World!";
        byte[] contents = responseBody.getBytes(StandardCharsets.UTF_8);

        HttpResponse response = new HttpResponse(HttpStatus.OK, new ResponseHeader(), contents);

        //when
        byte[] result = Formatter.toResponseFormat(response);
        byte[] expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 12",
                "",
                "Hello World!"
        ).getBytes(StandardCharsets.UTF_8);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("ResponseBody가 없는 HttpResponse를 바이트 출력 형식에 맞게 포맷팅한다.")
    void toResponseFormat_withoutResponseBody() {
        //given
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.OK);

        //when
        byte[] result = Formatter.toResponseFormat(response);
        byte[] expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 0",
                "",
                ""
        ).getBytes(StandardCharsets.UTF_8);

        //then
        assertThat(result).isEqualTo(expected);
    }
}
