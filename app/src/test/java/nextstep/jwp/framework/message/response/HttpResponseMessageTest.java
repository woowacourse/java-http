package nextstep.jwp.framework.message.response;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.common.HttpVersion;
import nextstep.jwp.framework.message.HeaderFields;
import nextstep.jwp.framework.message.MessageBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseMessageTest {

    @DisplayName("HttpResponseMessage 생성")
    @Test
    void create() {
        // given
        StatusLine statusLine = statusLine();
        ResponseHeader responseHeader = responseHeader();
        MessageBody responseBody = responseBody();

        // when
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(statusLine, responseHeader, responseBody);

        // then
        assertThat(httpResponseMessage.getStartLine()).isEqualTo(statusLine);
        assertThat(httpResponseMessage.getHeader()).isEqualTo(responseHeader);
        assertThat(httpResponseMessage.getBody()).isEqualTo(responseBody);
    }

    @DisplayName("HttpResponseMessage 를 byte[] 로 변환")
    @Test
    void toBytes() {
        // given
        String responseMessage = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "hello world!");
        byte[] expect = responseMessage.getBytes();
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(statusLine(), responseHeader(), responseBody());

        // when
        byte[] bytes = httpResponseMessage.toBytes();

        // then
        assertThat(bytes).isEqualTo(expect);
    }

    @DisplayName("equals 와 hashCode 검증")
    @Test
    void equalsAndHashCode() {
        // given
        StatusLine statusLine = statusLine();
        ResponseHeader responseHeader = responseHeader();
        MessageBody responseBody = responseBody();

        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(statusLine, responseHeader, responseBody);
        HttpResponseMessage otherHttpResponseMessage = new HttpResponseMessage(statusLine, responseHeader, responseBody);

        // when, then
        assertThat(httpResponseMessage).isEqualTo(otherHttpResponseMessage)
                .hasSameHashCodeAs(otherHttpResponseMessage);
    }

    private StatusLine statusLine() {
        return new StatusLine(HttpVersion.HTTP_1_1, HttpStatusCode.OK);
    }

    private ResponseHeader responseHeader() {
        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "12");
        HeaderFields headerFields = HeaderFields.from(headerParams);
        return new ResponseHeader(headerFields);
    }


    private MessageBody responseBody() {
        return MessageBody.from("hello world!");
    }
}
