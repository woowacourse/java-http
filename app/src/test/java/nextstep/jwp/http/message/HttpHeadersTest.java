package nextstep.jwp.http.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("문자열 형식으로 HttpHeaders 객체를 만들 수 있다.")
    @Test
    void createByString_변환_성공() {
        // given
        String httpHeaders = String.join("\r\n",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "");
        Set<String> keys = Set.of("Host", "Connection", "Content-Length", "Content-Type", "Accept");
        List<String> values = List.of("keep-alive", "80", "application/x-www-form-urlencoded", "*/*");

        // when
        HttpHeaders actual = HttpHeaders.createByString(httpHeaders);

        // then
        assertThat(actual.asString()).isEqualTo(httpHeaders);
        assertThat(actual.getHeaders().keySet()).containsAll(keys);
        assertThat(actual.getHeaders().values()).containsAll(values);
    }

    @DisplayName("HttpHeader이름으로 Header값을 찾을 수 있다.")
    @Test
    void getHeaderByName_찾기_성공() {
        // given
        String httpHeaders = String.join("\r\n",
            "Host: localhost:8080",
            "Accept: */*",
            "");

        // when
        HttpHeaders actual = HttpHeaders.createByString(httpHeaders);

        // then
        assertThat(actual.getHeaderByName("Host"))
            .get()
            .isEqualTo("localhost:8080");
        assertThat(actual.getHeaderByName("Accept"))
            .get()
            .isEqualTo("*/*");
    }

    @DisplayName("HttpHeader이름의 Header가 존재하지 않을 경우 Optional.empty를 반환한다.")
    @Test
    void getHeaderByName_찾기_실패() {
        // given
        String httpHeaders = String.join("\r\n",
            "Host: localhost:8080",
            "");

        // when
        HttpHeaders actual = HttpHeaders.createByString(httpHeaders);

        // then
        assertThat(actual.getHeaderByName("Accept"))
            .isSameAs(Optional.empty());
    }
}
