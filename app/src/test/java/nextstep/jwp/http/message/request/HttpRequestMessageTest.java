package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.MessageBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestMessageTest {

    @DisplayName("Header는 존재, Body는 없는 HttpRequestMessage 생성")
    @Test
    void createWithNoBody() {
        // given
        final String headerString = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        final String requestMessage = String.join("\r\n",
                headerString,
                "",
                "");

        RequestHeader expectedRequestHeader = RequestHeader.from(headerString);
        MessageBody expectedMessageBody = new MessageBody();

        // when
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestMessage);

        // then
        assertThat(httpRequestMessage.getHeader()).isEqualTo(expectedRequestHeader);
        assertThat(httpRequestMessage.getBody()).isEqualTo(expectedMessageBody);
    }

    @DisplayName("Header와 Body가 둘 다 존재하는 HttpRequestMessage 생성")
    @Test
    void createWithBody() {
        // given
        String requestMessage = makePostRequestMessage();
        String headerString = makePostRequestHeader();
        String bodyString = makePostRequestBody();

        RequestHeader expectedRequestHeader = RequestHeader.from(headerString);
        MessageBody expectedMessageBody = new MessageBody(bodyString);

        // when
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestMessage);

        // then
        assertThat(httpRequestMessage.getHeader()).isEqualTo(expectedRequestHeader);
        assertThat(httpRequestMessage.getBody()).isEqualTo(expectedMessageBody);
    }

    @DisplayName("문자열의 RequestMessage로 HttpRequestMessage 생성")
    @Test
    void createWithRequestString() {
        // given
        String headerString = makePostRequestHeader();
        String bodyString = makePostRequestBody();
        HttpRequestMessage expect = new HttpRequestMessage(RequestHeader.from(headerString), new MessageBody(bodyString));

        String requestMessage = makePostRequestMessage();

        // when
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestMessage);

        // then
        assertThat(httpRequestMessage).isEqualTo(expect);
    }

    @DisplayName("Header의 RequestUri를 변경한다.")
    @Test
    void changeRequestUri() {
        // given
        String requestMessage = makePostRequestMessage();
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestMessage);

        // when
        httpRequestMessage.changeRequestUri("/hello");

        // then
        assertThat(httpRequestMessage.requestUri()).isEqualTo("/hello");
    }

    private String makePostRequestMessage() {
        return String.join("\r\n",
                makePostRequestHeader(),
                "",
                makePostRequestBody());
    }
    private String makePostRequestHeader() {
        return String.join("\r\n",
                "POST /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");
    }

    private String makePostRequestBody() {
        return "hello world";
    }
}