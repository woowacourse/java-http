package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpMethodTest")
class HttpMethodTest {

    @Test
    @DisplayName("POST 는 true 를 GET 은 false 를 반환해야 한다.")
    void isBody() {
        assertThat(HttpMethod.GET.isBody()).isFalse();
        assertThat(HttpMethod.POST.isBody()).isTrue();
    }
}