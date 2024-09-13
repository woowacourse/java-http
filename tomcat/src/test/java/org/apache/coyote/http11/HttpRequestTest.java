package org.apache.coyote.http11;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("URL 확장자에 따라 ContentType을 반환한다.")
    @Test
    void getContentType() {
        // given
        String path = "/login";
        String fileExtension = ".css";
        String expectedFileExtension = "css";

        RequestLine requestLine = new RequestLine(Method.GET, path + fileExtension, "HTTP/1.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders);

        // when
        String contentType = httpRequest.getContentType();

        // then
        Assertions.assertThat(contentType).endsWith(expectedFileExtension);
    }

    @DisplayName("URL 확장자가 존재하지 않으면 ContentType에서 text/html을 반환한다.")
    @Test
    void getContentType_whenFileExtensionNotExist() {
        // given
        String path = "/login";
        String expectedContentType = "text/html";

        RequestLine requestLine = new RequestLine(Method.GET, path, "HTTP/1.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders);

        // when
        String contentType = httpRequest.getContentType();

        // then
        Assertions.assertThat(contentType).endsWith(expectedContentType);
    }
}
