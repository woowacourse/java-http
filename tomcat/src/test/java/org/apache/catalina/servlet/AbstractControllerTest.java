package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.body.HttpResponseBody;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpResponseLine;
import org.apache.catalina.http.startline.HttpStatus;
import org.apache.catalina.http.startline.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private static AbstractController getController() {
        return new AbstractController() {
            @Override
            public void service(HttpRequest request, HttpResponse response) {
            }
        };
    }

    @DisplayName("주어진 페이지로 redirect하고, 302 상태코드를 응답한다.")
    @Test
    void redirectTo() {
        // given
        HttpResponseLine responseLine = new HttpResponseLine(HttpVersion.HTTP11);
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpResponseBody responseBody = new HttpResponseBody();
        HttpResponse response = new HttpResponse(responseLine, responseHeaders, responseBody);

        AbstractController controller = getController();

        // when
        controller.redirectTo(response, "/index");

        // then
        assertThat(responseLine.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(responseHeaders.get(HttpHeader.LOCATION)).isEqualTo("/index");
    }

    @DisplayName("정적 파일을 응답한다.")
    @Test
    void responseResource() {
        // given
        HttpResponseLine responseLine = new HttpResponseLine(HttpVersion.HTTP11);
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpResponseBody responseBody = new HttpResponseBody();
        HttpResponse response = new HttpResponse(responseLine, responseHeaders, responseBody);

        AbstractController controller = getController();
        // when
        controller.responseResource(response, "/index.html");

        // then
        assertThat(responseHeaders.get(HttpHeader.CONTENT_TYPE)).contains("text/html");
        assertThat(responseBody.getValue()).contains("<title>대시보드</title>");
    }
}