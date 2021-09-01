package nextstep.jwp.web.network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class URITest {

    @DisplayName("URI 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final String value = "/login";

        // when // then
        assertThatCode(() -> new URI(value))
                .doesNotThrowAnyException();
    }

    @DisplayName("URI에서 path를 추출한다 - 성공")
    @Test
    void getPath() {
        // given
        final URI uri = new URI("/login");

        // when
        final String path = uri.getPath();

        // then
        assertThat(path).isEqualTo("/login");
    }

    @DisplayName("URI에 query가 존재해도 path를 추출할 수 있다 - 성공")
    @Test
    void getPathWhenQueryExists() {
        // given
        final URI uri = new URI("/login?account=gugu&password=password");

        // when
        final String path = uri.getPath();

        // then
        assertThat(path).isEqualTo("/login");
    }

    @DisplayName("URI에서 query를 추출한다 - 성공")
    @Test
    void hasQuery() {
        // given
        final URI uri = new URI("/login?account=gugu&password=password");

        // when
        final String query = uri.getQuery();

        // then
        assertThat(query).isEqualTo("account=gugu&password=password");
    }

    @DisplayName("URI는 동등성을 보장한다 - 성공")
    @Test
    void equals() {
        // given
        final URI firstURI = new URI("/index.html");
        final URI secondURI = new URI("/index.html");

        // when // then
        assertThat(firstURI).isEqualTo(secondURI);
    }
}