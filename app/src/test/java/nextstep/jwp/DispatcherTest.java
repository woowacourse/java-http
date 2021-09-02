package nextstep.jwp;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DispatcherTest {

    private final Assembler container = new Assembler();
    private final Dispatcher dispatcher = container.dispatcher();

    @DisplayName("ControllerMapping 후 요청 처리 확인")
    @Test
    void dispatchController() {
        // given
        final String requestBody = "account=corgi&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /register HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.FOUND);
    }

    @DisplayName("ResourceHandler 매핑 후, 요청 처리 확인")
    @Test
    void dispatchResourceHandler() {
        // given
        final String requestBody = "account=corgi&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("매핑된 handler가 없는 경우 Not found 출력")
    @Test
    void notFound() {
        // given
        RequestLine requestLine = RequestLine.of("GET /invalid.html HTTP/1.1");

        HttpRequest httpRequest = new HttpRequest(requestLine, RequestHeaders.of(Collections.emptyList()), "");
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @DisplayName("로그인 실패에 Unauthorized 출력")
    @Test
    void unauthorized() {
        // given
        final String requestBody = "account=account&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("서버 에러시 INTERNAL SERVER ERROR 출력")
    @Test
    void internalServerError() {
        // given
        final String requestBody = "account=corgi&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        Dispatcher dispatcher = new Dispatcher(null, container.viewSolver());

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("쿠키에 Session이 존재하지 않는 경우 SessionId를 응답한다")
    @Test
    void responseSessionId() {
        // given
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHeader("Set-Cookie")).isNotNull();
    }
}