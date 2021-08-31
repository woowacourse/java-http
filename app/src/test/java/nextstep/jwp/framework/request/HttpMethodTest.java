package nextstep.jwp.framework.request;

import nextstep.jwp.framework.request.details.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {

    @DisplayName("정적 팩토리 메서드로 HttpMethod 조회 시 항상 같은 객체가 반환된다.")
    @Test
    void of() {
        final HttpMethod getMethod1 = HttpMethod.of("GET");
        final HttpMethod getMethod2 = HttpMethod.of("GET");
        assertThat(getMethod1).isSameAs(getMethod2);

        final HttpMethod postMethod1 = HttpMethod.of("POST");
        final HttpMethod postMethod2 = HttpMethod.of("POST");
        assertThat(postMethod1).isSameAs(postMethod2);
    }

    @DisplayName("GET, POST 방식 의외에는 예외가 발생한다.")
    @Test
    void getPostOnly() {
        assertThatThrownBy(() -> HttpMethod.of("OPTION"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}