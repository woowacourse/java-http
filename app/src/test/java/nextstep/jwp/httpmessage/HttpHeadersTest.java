package nextstep.jwp.httpmessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpHeaders 기능 테스트")
class HttpHeadersTest {

    @Test
    void find() {
        //given
        final String key = "Content-Type";
        final String value = "text/html;charset=utf-8";
        final Map<String, String> headers = new HashMap<>();
        //when
        headers.put(key, value);
        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        final String actual = httpHeaders.find(key);
        //then
        assertThat(actual).isEqualTo(value);
        assertThat(httpHeaders.size()).isEqualTo(1);
    }

    @Test
    void return_null_when_key_not_found() {
        //given
        final String key = "Content-Type";
        final String value = "text/html;charset=utf-8";
        final Map<String, String> headers = new HashMap<>();
        //when
        headers.put(key, value);
        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        final String actual = httpHeaders.find("someThing");
        //then
        assertThat(actual).isNull();
        assertThat(httpHeaders.size()).isEqualTo(1);
    }
}