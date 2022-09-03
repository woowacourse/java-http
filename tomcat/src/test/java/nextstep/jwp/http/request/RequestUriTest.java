package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RequestUriTest {

    @Test
    void RequestUri를_반환할_수_있다() {
        String uri = "/index.html";

        assertThat(RequestUri.create(uri)).isInstanceOf(RequestUri.class);
    }

    @Test
    void uri의_path값을_파싱할_수_있다() {
        String uri = "/login?account=rookie&password=password";

        RequestUri requestUri = RequestUri.create(uri);

        assertThat(requestUri.getUri()).isEqualTo("/login");
    }

    @Test
    void 쿼리_파라미터를_파싱할_수_있다() {
        String uri = "/login?account=rookie&password=password";

        RequestUri requestUri = RequestUri.create(uri);

        assertThat(requestUri.getQueryParameters()).isEqualTo(
            Map.of("account", "rookie", "password", "password"));
    }

    @Test
    void 쿼리_파라미터가_없는경우_빈_파라미터를_반환한다() {
        String uri = "/login";

        RequestUri requestUri = RequestUri.create(uri);

        assertThat(requestUri.getQueryParameters()).isEqualTo(new HashMap<>());
    }
}
