package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.MessageBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestMessageTest {

    @DisplayName("Header는 존재, Body는 없는 HttpRequestMessage 생성")
    @Test
    void createWithBody() {
        // given
        final String headerString = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        final String httpRequest = String.join("\r\n",
                headerString,
                "",
                "");

        RequestHeader expectedRequestHeader = RequestHeader.from(headerString);

        // then
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(httpRequest);

        // then
        assertThat(httpRequestMessage.getHeader()).isEqualTo(expectedRequestHeader);
        assertThat(httpRequestMessage.getBody()).isEmpty();
    }

    @DisplayName("Header와 Body가 둘 다 존재하는 HttpRequestMessage 생성")
    @Test
    void createWithNoBody() {
        // given
        final String headerString = String.join("\r\n",
                "POST /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");
        final String bodyString = "hello world";

        final String httpRequest = String.join("\r\n",
                headerString,
                "",
                bodyString);

        RequestHeader expectedRequestHeader = RequestHeader.from(headerString);
        MessageBody expectedMessageBody = new MessageBody(bodyString);

        // when
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(httpRequest);

        // then
        assertThat(httpRequestMessage.getHeader()).isEqualTo(expectedRequestHeader);
        assertThat(httpRequestMessage.getBody()).get().isEqualTo(expectedMessageBody);
    }
}