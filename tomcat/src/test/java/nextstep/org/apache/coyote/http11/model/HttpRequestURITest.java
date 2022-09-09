package nextstep.org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.model.HttpRequestURI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestURITest {

    @Test
    @DisplayName("주어진 uri로 HttpRequestURI 객체를 생성한다.")
    void createHttpRequestURI() {
        // given
        String uri = "/index.css";

        // when
        HttpRequestURI httpRequestURI = HttpRequestURI.from(uri);

        // then
        assertAll(
            () -> assertThat(httpRequestURI.getPath()).isEqualTo("/index.css"),
            () -> assertThat(httpRequestURI.getExtension()).isEqualTo("css")
        );
    }

    @Test
    @DisplayName("확장자가 없는 uri에 대해 html 확장자를 부여한다.")
    void addHtmlExtension() {
        // given
        String uri = "/login";

        // when
        HttpRequestURI httpRequestURI = HttpRequestURI.from(uri);

        // then
        assertAll(
            () -> assertThat(httpRequestURI.getPath()).isEqualTo("/login.html"),
            () -> assertThat(httpRequestURI.getExtension()).isEqualTo("html")
        );
    }
}
