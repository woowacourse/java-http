package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.request.RequestURL;
import org.junit.jupiter.api.Test;

public class RequestURLTest {

    @Test
    void createRequestURL() {
        final RequestURL requestURL = RequestURL.from("/login?account=brorae&password=password");

        assertAll(
                () -> assertThat(requestURL.getPath()).isEqualTo("/login"),
                () -> assertThat(requestURL.getQueryParams()).hasSize(2)
        );
    }
}
