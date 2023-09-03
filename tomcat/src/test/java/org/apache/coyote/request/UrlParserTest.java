package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlParserTest {

    @Test
    @DisplayName("주어진 InputStream의 url을 파싱하여 url을 생성한다.")
    void getResourceUrl_normal() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("GET /index.html HTTP/1.1".getBytes());
        UrlParser urlParser = new UrlParser(inputStream);

        URL resourceUrl = urlParser.getResourceUrl();

        assertThat(resourceUrl.getFile()).contains("/index.html");
    }

    @Test
    @DisplayName("주어진 url이 /라면 index.html에 대한 url을 생성한다.")
    void getResourceUrl_home() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("GET / HTTP/1.1".getBytes());
        UrlParser urlParser = new UrlParser(inputStream);

        URL resourceUrl = urlParser.getResourceUrl();

        assertThat(resourceUrl.getFile()).contains("/index.html");
    }


    @Test
    @DisplayName("주어진 url의 파일이 없다면 null을 반환한다.")
    void getResourceUrl_noFile() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("GET /trestset/qweqsdae/asdawdqd/qwdqweqw HTTP/1.1".getBytes());
        UrlParser urlParser = new UrlParser(inputStream);

        URL resourceUrl = urlParser.getResourceUrl();

        assertThat(resourceUrl).isNull();
    }
}
