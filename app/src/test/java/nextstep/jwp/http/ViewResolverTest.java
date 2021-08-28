package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @Test
    @DisplayName("HttpRequest로 요청한 값이 Static 폴더에 존재할 경우 true를 반환한다.")
    void isExisting() {
        // given
        String statusLine = "GET /login.html HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);
        ViewResolver viewResolver = new ViewResolver(httpRequest);

        // when
        boolean actual = viewResolver.isExisting();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("HttpRequest로 요청한 값이 Static 폴더에 존재하지 않을 경우 true를 반환한다.")
    void isExisting_false() {
        // given
        String statusLine = "GET /aaaaa.html HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);
        ViewResolver viewResolver = new ViewResolver(httpRequest);

        // when
        boolean actual = viewResolver.isExisting();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("HttpRequest로 요청한 값이 Static 폴더에 존재할 경우 200으로 응답한다.")
    void resolve_200() {
        // given
        String statusLine = "GET /login.html HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);
        ViewResolver viewResolver = new ViewResolver(httpRequest);

        // when
        HttpResponse actual = viewResolver.resolve();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("HttpRequest로 요청한 값이 Static 폴더에 존재하지 않을 경우 404로 응답한다.")
    void resolve_404() {
        // given
        String statusLine = "GET /aaaa.html HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);
        ViewResolver viewResolver = new ViewResolver(httpRequest);

        // when
        HttpResponse actual = viewResolver.resolve();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}