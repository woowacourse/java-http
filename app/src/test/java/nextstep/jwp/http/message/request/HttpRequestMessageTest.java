package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.MessageBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestMessageTest {

    @DisplayName("HttpRequestMessage 생성")
    @Test
    void create() {
        // given
        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = new RequestHeader(requestHeaderMessage());
        MessageBody requestBody = new MessageBody(requestBodyMessage());

        // when
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // then
        assertThat(httpRequestMessage.getStartLine()).isEqualTo(requestLine);
        assertThat(httpRequestMessage.getHeader()).isEqualTo(requestHeader);
        assertThat(httpRequestMessage.getBody()).isEqualTo(requestBody);
    }

    @DisplayName("HttpRequestMessage 를 byte[] 로 변환")
    @Test
    void toBytes() {
        // given
        String requestMessage = String.join("\r\n",
                requestLineMessage(),
                requestHeaderMessage(),
                "",
                requestBodyMessage());

        byte[] expect = requestMessage.getBytes();

        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = new RequestHeader(requestHeaderMessage());
        MessageBody requestBody = new MessageBody(requestBodyMessage());
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // when
        byte[] bytes = httpRequestMessage.toBytes();
        
        // then
        assertThat(bytes).isEqualTo(expect);
    }

    private String requestLineMessage() {
        return "POST /index.html HTTP/1.1";
    }

    private String requestHeaderMessage() {
        return String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 12");
    }

    private String requestBodyMessage() {
        return "hello world!";
    }
}
