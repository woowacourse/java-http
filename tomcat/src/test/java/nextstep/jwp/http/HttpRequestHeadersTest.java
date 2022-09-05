package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpRequestHeadersTest {

    @Test
    void HttpRequestHeaders를_생성한다() {
        HttpRequestHeaders expected = new HttpRequestHeaders(Map.of("Host", "www.localhost.com", "Content-Length", "40"));
        HttpRequestHeaders actual = HttpRequestHeaders.from(List.of("Host: www.localhost.com", "Content-Length: 40"));

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"Content-Length: 40,true", "Host: www.localhost.com,false"})
    void contentLength가_있는지_확인한다(final String input, final boolean expected) {
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(List.of(input));
        boolean actual = httpRequestHeaders.isContainContentLength();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void contentLength를_반환한다() {
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(List.of("Content-Length: 40"));
        int actual = httpRequestHeaders.contentLength();

        assertThat(actual).isEqualTo(40);
    }

    @Test
    void contentLength가_없는데_반환하려하는_경우_예외가_발생한다() {
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(List.of("Host: www.localhost.com"));
        assertThatThrownBy(httpRequestHeaders::contentLength)
                .isInstanceOf(UncheckedServletException.class);
    }
}
