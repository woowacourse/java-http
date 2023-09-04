package org.apache.coyote.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticFileHandlerTest {

  private StaticFileHandler staticFileHandler = new StaticFileHandler();

  @Test
  @DisplayName("canHandle() : URI가 js, html, js, ico 로 끝난다면 true를 반환할 수 있다.")
  void test_canHandle() throws Exception {
    //given
    final String request = "GET /css/styles.css";

    //when & then
    assertTrue(staticFileHandler.canHandle(HttpRequest.from(request)));
  }

  @Test
  @DisplayName("handle() : URI가 js, html, js, ico 로 끝난다면 정상적으로 resource를 반환할 수 있다.")
  void test_handle() throws Exception {
    //given
    final String request = "GET /js/scripts.js";
    final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");

    final String expected = "HTTP/1.1 200 OK \r\n" +
        "Content-Type: text/javascript;charset=utf-8 \r\n" +
        "Content-Length: 976 \r\n" +
        "\r\n" +
        new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

    //when
    final String actual = staticFileHandler.handle(HttpRequest.from(request));

    //then
    assertEquals(expected, actual);
  }
}
