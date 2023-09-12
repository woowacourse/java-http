package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.coyote.request.Cookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestLine;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.SessionManager;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseHeader;
import org.apache.coyote.response.HttpResponseStatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

  private LoginHandler loginHandler = new LoginHandler();

  @Test
  @DisplayName("canHandle() : URI가 /login으로 시작하고, HTTP 요청이 GET일 경우 true를 반환할 수 있다.")
  void test_canHandle() throws Exception {
    //given
    final Cookie cookie = Cookie.from("");
    final HttpRequestLine httpRequestLine = new HttpRequestLine("GET", "/notLogin");
    final HttpRequest httpRequest = new HttpRequest(
        httpRequestLine,
        null,
        null,
        cookie,
        new SessionManager()
    );

    //when
    assertFalse(loginHandler.canHandle(httpRequest));
  }

  @Test
  @DisplayName("canHandle() : URI가 /login으로 시작하지 않으면 false를 반환할 수 있다.")
  void test_canHandle_false() throws Exception {
    //given
    final Cookie cookie = Cookie.from("");
    final HttpRequestLine httpRequestLine = new HttpRequestLine("GET", "/notLogin");
    final HttpRequest httpRequest = new HttpRequest(
        httpRequestLine,
        null,
        null,
        cookie,
        new SessionManager()
    );

    //when
    assertFalse(loginHandler.canHandle(httpRequest));
  }

  @Test
  @DisplayName("handle() : GET /login 요청할 경우 사용자 로그인 페이지를 띄울 수 있다.")
  void test_handle() throws Exception {
    //given
    final RequestBody requestBody = RequestBody.from("account=gugu&password=password");
    final HttpRequestLine httpRequestLine = new HttpRequestLine("POST", "/login");
    final Cookie cookie = Cookie.from("");
    final HttpRequest httpRequest = new HttpRequest(
        httpRequestLine,
        null,
        requestBody,
        cookie,
        new SessionManager()
    );

    final HttpResponse actual = new HttpResponse();
    actual.setHttpResponseHeader(new HttpResponseHeader().addCookie(cookie)
        .sendRedirect("http://localhost:8080/index.html"));
    actual.setHttpResponseStatusLine(HttpResponseStatusLine.redirect());

    //when
    final HttpResponse expect = new HttpResponse();
    loginHandler.handle(httpRequest, expect);

    //then
    final HttpResponseHeader expectHeader = expect.getHttpResponseHeader();
    final HttpResponseStatusLine expectStatusLine = expect.getHttpResponseStatusLine();

    assertAll(
        () -> assertTrue(expectHeader.getValues().containsKey("Set-Cookie")),
        () -> assertEquals("/index.html", expectHeader.getValues().get("Location")),
        () -> assertThat(actual.getHttpResponseStatusLine())
            .usingRecursiveComparison()
            .isEqualTo(expectStatusLine)
    );
  }
}
