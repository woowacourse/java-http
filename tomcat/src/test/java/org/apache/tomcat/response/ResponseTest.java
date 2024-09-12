package org.apache.tomcat.response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import org.apache.coyote.http11.Response;
import org.apache.tomcat.http.common.Version;
import org.apache.tomcat.http.common.body.TextTypeBody;
import org.apache.tomcat.http.response.ResponseHeader;
import org.apache.tomcat.http.response.StatusCode;
import org.apache.tomcat.http.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    @DisplayName("http 응답 메세지 생성한다.")
    void generate_http_response_message() {
        // given
        final var responseLine = new StatusLine(new Version(1, 1), new StatusCode("OK", 200));
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Content-type", "text/plain");
        responseHeader.put("fram", "6");
        responseHeader.put("Authorization", "Bearer=token");
        final var dummyHtml = "<html>\r\n <p></p>";
        final var sut = new Response(responseLine, responseHeader, new TextTypeBody(dummyHtml));

        // when & then
        assertThatCode(sut::getResponseText)
                .doesNotThrowAnyException();
    }

}
