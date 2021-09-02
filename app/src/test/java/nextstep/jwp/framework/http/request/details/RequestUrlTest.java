package nextstep.jwp.framework.http.request.details;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUrlTest {

    @DisplayName("RequestUrl을 넘겨주면 url과 queryParameter를 파싱한다.")
    @Test
    void urlAndQueryParam() {
        final RequestUrl requestUrl = RequestUrl.of("/hello?key1=value1&key2=value2");
        assertThat(requestUrl.getUrl()).isEqualTo("/hello");
        assertThat(requestUrl.getQueryParam().searchValue("key1")).isEqualTo("value1");
        assertThat(requestUrl.getQueryParam().searchValue("key2")).isEqualTo("value2");
    }

    @DisplayName("RequestUrl에 QueryString이 없다면 QueryParameter 객체 null을 반환한다.")
    @Test
    void requestUrl() {
        final RequestUrl requestUrl = RequestUrl.of("/hello");
        assertThat(requestUrl.getUrl()).isEqualTo("/hello");
        assertThat(requestUrl.getQueryParam()).isNull();
    }
}
