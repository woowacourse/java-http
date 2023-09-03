package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.http11.response.ContentType.HTML;
import static org.apache.coyote.http11.response.StatusCode.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    @DisplayName("Response를 생성한다.")
    void createResponse() {
        //given
        final Response response = Response.of(HTTP_1_1, OK, HTML, "content");

        //when
        final String parsedResponse = response.parse();

        //then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK "
                , "Content-Type: text/html;charset=utf-8 "
                , "Content-Length: 7 "
                , ""
                , "content");
        assertThat(parsedResponse).isEqualTo(expected);
    }

}
