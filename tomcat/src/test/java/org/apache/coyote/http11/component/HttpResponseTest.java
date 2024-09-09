package org.apache.coyote.http11.component;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import org.apache.coyote.http11.component.common.Version;
import org.apache.coyote.http11.component.response.HttpResponse;
import org.apache.coyote.http11.component.response.ResponseHeader;
import org.apache.coyote.http11.component.response.ResponseLine;
import org.apache.coyote.http11.component.response.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("http 응답 메세지 생성한다.")
    void generate_http_response_message() {
        // given
        final var responseLine = new ResponseLine(new Version(1, 1), new StatusCode("OK", 200));
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Content-type", "text/plain");
        responseHeader.put("fram", "6");
        responseHeader.put("Authorization", "Bearer=token");
        final var dummyHtml = "<html>\r\n <p></p>";
        final var sut = new HttpResponse(responseLine, responseHeader, dummyHtml);

        // when & then
        assertThatCode(sut::getResponseText)
                .doesNotThrowAnyException();
    }

}
