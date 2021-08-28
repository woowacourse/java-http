package nextstep.jwp.infrastructure.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class URITest {

    @DisplayName("uri 파싱")
    @Test
    void of() {
        final URI uri = URI.of("/login?account=gugu&password=password");

        assertThat(uri.getBaseUri()).isEqualTo("/login");
        assertThat(uri.hasKeys(Arrays.asList("account", "password"))).isTrue();
        assertThat(uri.getValue("account")).isEqualTo("gugu");
        assertThat(uri.getValue("password")).isEqualTo("password");
    }
}