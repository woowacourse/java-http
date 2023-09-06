package org.apache.coyote.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

  private LoginHandler loginHandler = new LoginHandler();

  @Test
  @DisplayName("canHandle() : URI가 /login으로 시작하고, HTTP 요청이 GET일 경우 true를 반환할 수 있다.")
  void test_canHandle() throws Exception {
    //given
    final String request = "GET /login";

    //when
    assertTrue(loginHandler.canHandle(HttpRequest.from(request)));
  }

  @Test
  @DisplayName("canHandle() : URI가 /login으로 시작하지 않으면 false를 반환할 수 있다.")
  void test_canHandle_false() throws Exception {
    //given
    final String request = "GET /notLogin";

    //when
    assertFalse(loginHandler.canHandle(HttpRequest.from(request)));
  }

  @Test
  @DisplayName("handle() : GET /login 요청할 경우 사용자 로그인 페이지를 띄울 수 있다.")
  void test_handle() throws Exception {
    //given
    final String request = "GET /login?account=gugu&password=password";
    final URL resource = getClass().getClassLoader().getResource("static/login.html");

    final String expected = "HTTP/1.1 200 OK \r\n" +
        "Content-Type: text/html;charset=utf-8 \r\n" +
        "Content-Length: 3796 \r\n" +
        "\r\n"+
        new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

    //when
    final String actual = loginHandler.handle(HttpRequest.from(request));

    //then
    assertEquals(expected, actual);
  }
}
