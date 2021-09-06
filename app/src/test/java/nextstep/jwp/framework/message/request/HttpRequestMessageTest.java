package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.framework.session.HttpSession;
import nextstep.jwp.framework.session.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestMessageTest {

    @DisplayName("HttpRequestMessage 생성")
    @Test
    void create() {
        // given
        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = RequestHeader.from(requestHeaderMessage());
        MessageBody requestBody = MessageBody.from(requestBodyMessage());

        // when
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // then
        assertThat(httpRequestMessage.getStartLine()).isEqualTo(requestLine);
        assertThat(httpRequestMessage.getHeader()).isEqualTo(requestHeader);
        assertThat(httpRequestMessage.getBody()).isEqualTo(requestBody);
    }

    @DisplayName("쿠키에 유효한 세션 ID 가지는 경우 세션을 가져오고 세션을 새로고침 한다.")
    @Test
    void takeSession() throws InterruptedException {
        // given
        String sessionId = sessionId();
        HttpSession savedHttpSession = new HttpSession(sessionId);
        HttpSessions.add(sessionId, savedHttpSession);
        long beforeAccessTime = savedHttpSession.getAccessTime();
        Thread.sleep(1);

        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = RequestHeader.from(requestHeaderMessageWithSessionId());
        MessageBody requestBody = MessageBody.from(requestBodyMessage());
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // when
        HttpSession httpSession = httpRequestMessage.takeSession();

        // then
        assertThat(httpSession).isSameAs(savedHttpSession);
        assertThat(savedHttpSession.getAccessTime()).isGreaterThan(beforeAccessTime);

        // tearDown
        HttpSessions.remove(sessionId);
    }

    @DisplayName("쿠키에 세션 ID가 없는 경우 유효하지 않은 세션을 가져온다.")
    @Test
    void takeSessionWithNoIdInCookie() {
        // given
        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = RequestHeader.from(requestHeaderMessage());
        MessageBody requestBody = MessageBody.from(requestBodyMessage());
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // when
        HttpSession httpSession = httpRequestMessage.takeSession();

        // then
        assertThat(httpSession.isInvalid()).isTrue();
    }

    @DisplayName("쿠키에 세션 ID 는 있지만 세션 저장소에 해당 ID의 세션이 없는 경우 유효하지 않은 세션을 가져온다.")
    @Test
    void takeSessionWithNoIdInSessions() {
        // given
        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = RequestHeader.from(requestHeaderMessageWithSessionId());
        MessageBody requestBody = MessageBody.from(requestBodyMessage());
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // when
        HttpSession httpSession = httpRequestMessage.takeSession();

        // then
        assertThat(httpSession.isInvalid()).isTrue();
    }

    @DisplayName("쿠키에 세션 ID 가 있고 세션 저장소에도 해당 ID의 세션이 있지만, 만료된 세션인 경우, 삭제하고 유효하지 않은 세션을 가져온다.")
    @Test
    void takeSessionWithExpiredSession() {
        // given
        String sessionId = sessionId();
        HttpSession savedHttpSession = new HttpSession(sessionId, -1);
        HttpSessions.add(sessionId, savedHttpSession);

        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = RequestHeader.from(requestHeaderMessageWithSessionId());
        MessageBody requestBody = MessageBody.from(requestBodyMessage());
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // when
        HttpSession httpSession = httpRequestMessage.takeSession();

        // then
        assertThat(httpSession.isInvalid()).isTrue();
        assertThat(HttpSessions.find(sessionId)).isEmpty();
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
        RequestHeader requestHeader = RequestHeader.from(requestHeaderMessage());
        MessageBody requestBody = MessageBody.from(requestBodyMessage());
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // when
        byte[] bytes = httpRequestMessage.toBytes();

        // then
        assertThat(bytes).isEqualTo(expect);
    }

    @DisplayName("equals 와 hashCode 검증")
    @Test
    void equalsAndHashCode() {
        // given
        RequestLine requestLine = RequestLine.from(requestLineMessage());
        RequestHeader requestHeader = RequestHeader.from(requestHeaderMessage());
        MessageBody requestBody = MessageBody.from(requestBodyMessage());

        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);
        HttpRequestMessage otherHttpRequestMessage = new HttpRequestMessage(requestLine, requestHeader, requestBody);

        // when, then
        assertThat(httpRequestMessage).isEqualTo(otherHttpRequestMessage)
                .hasSameHashCodeAs(otherHttpRequestMessage);
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

    private String requestHeaderMessageWithSessionId() {
        return String.join("\r\n",
                requestHeaderMessage(),
                "Cookie: JSESSIONID=" + sessionId());
    }

    private String requestBodyMessage() {
        return "hello world!";
    }

    private String sessionId() {
        return "656cef62-e3c4-40bc-a8df-94732920ed46";
    }
}
