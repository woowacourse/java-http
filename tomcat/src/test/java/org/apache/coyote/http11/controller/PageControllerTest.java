package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.apache.coyote.http11.controller.support.FileFinder;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PageControllerTest {

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/401.html", "/404.html", "/css/styles.css",
        "/js/scripts.js"})
    @DisplayName("올바른 요청이 들어오면 이에 해당하는 결과물을 반환한다.")
    void load_page(final String path) throws Exception {
        // given
        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.GET,
            path,
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(new HashMap<>()));

        final HttpResponse httpResponse = new HttpResponse();
        final PageController pageController = new PageController();

        // when
        pageController.doGet(httpRequest, httpResponse);

        // then
        final String body = FileFinder.find(path);
        final String bodyLength = String.valueOf(body.getBytes().length);
        String extension = path.split("\\.")[1];
        if (extension.equals("js")) {
            extension = "javascript";
        }

        final String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/" + extension + ";charset=utf-8 \r\n" +
            "Content-Length: " + bodyLength + " \r\n" +
            "\r\n" +
            body;

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/main.html", "/home.html"})
    @DisplayName("존재하지 않는 파일 요청이 들어오면 404 페이지를 발생한다.")
    void load_404_page(final String badPath) throws Exception {
        // given
        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.GET,
            badPath,
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(new HashMap<>()));

        final HttpResponse httpResponse = new HttpResponse();
        final PageController pageController = new PageController();

        // when
        pageController.doGet(httpRequest, httpResponse);

        // then
        final String body = FileFinder.find("/404.html");
        final String bodyLength = String.valueOf(body.getBytes().length);
        final String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: " + bodyLength + " \r\n" +
            "\r\n" +
            body;

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }
}
