package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpHeaderName;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.response.line.HttpStatus;
import org.apache.coyote.http.response.line.ResponseLine;
import org.apache.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Http 응답 클래스 테스트")
class HttpResponseTest {

    private HttpHeaders httpHeaders;
    private HttpMessageBody httpMessageBody;
    private ResponseLine responseLine;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpMessageBody = HttpMessageBody.createEmptyBody();
        responseLine = ResponseLine.createEmptyResponseLine();
    }

    @Test
    @DisplayName("OK 상태 코드와 파일을 읽어 본문을 설정한다.")
    void okTest() {
        // given
        HttpResponse httpResponse = new HttpResponse(responseLine, httpHeaders, httpMessageBody);
        String fileName = "static/index.html";

        // when
        httpResponse.ok(fileName);

        // then
        Assertions.assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpHeaders.getHeaderValue(HttpHeaderName.CONTENT_TYPE))
                        .isEqualTo(MimeType.from(fileName).getContentType() + HttpResponse.CHARSET_UTF_8),
                () -> assertThat(httpMessageBody.resolveBodyMessage()).isEqualTo(FileUtils.readFile(fileName))
        );
    }

    @Test
    @DisplayName("FOUND 상태 코드로 리다이렉션 응답을 생성한다.")
    void sendRedirectTest() {
        // given
        HttpResponse httpResponse = new HttpResponse(responseLine, httpHeaders, httpMessageBody);
        String location = "/redirected-path";

        // when
        httpResponse.sendRedirect(location);

        // then
        Assertions.assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpHeaders.getHeaderValue(HttpHeaderName.LOCATION)).isEqualTo(location)
        );
    }

    @Test
    @DisplayName("에러 상태 코드와 파일을 설정한다.")
    void sendErrorTest() {
        // given
        HttpResponse httpResponse = new HttpResponse(responseLine, httpHeaders, httpMessageBody);
        String fileName = "static/401.html";

        // when
        httpResponse.sendError(HttpStatus.UNAUTHORIZED, fileName);

        // then
        Assertions.assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(httpHeaders.getHeaderValue(HttpHeaderName.CONTENT_TYPE))
                        .isEqualTo(MimeType.from(fileName).getContentType() + HttpResponse.CHARSET_UTF_8),
                () -> assertThat(httpMessageBody.resolveBodyMessage()).isEqualTo(FileUtils.readFile(fileName))
        );
    }

    @Test
    @DisplayName("응답 메시지를 조합한다.")
    void resolveHttpMessageTest() {
        HttpResponse httpResponse = new HttpResponse(responseLine, httpHeaders, httpMessageBody);
        responseLine.setHttpStatus(HttpStatus.OK);
        httpHeaders.putHeader(HttpHeaderName.CONTENT_TYPE, "text/html");
        httpMessageBody.write("<html>Hello, world!</html>");

        String expectedMessage = String.join(
                "\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                "",
                "<html>Hello, world!</html>"
        );

        assertThat(httpResponse.resolveHttpMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("JSESSIONID 쿠키를 설정한다.")
    void setJsessionTest() {
        HttpResponse httpResponse = new HttpResponse(responseLine, httpHeaders, httpMessageBody);
        String sessionId = "abc123";

        httpResponse.setJsession(sessionId);

        assertThat(httpHeaders.getHeaderValue(HttpHeaderName.SET_COOKIE)).isEqualTo(
                HttpResponse.JSESSION_COOKIE_PREFIX + sessionId);
    }
}
