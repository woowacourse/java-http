package nextstep.org.apache.catalina.servlet.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.catalina.servlet.request.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("URI 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class URITest {

    @Test
    void 쿼리_파라미터를_가져올_수_있다() {
        // when
        URI uri = URI.from("/login?account=mallang&password=1234");

        // then
        assertThat(uri.uri()).isEqualTo("/login?account=mallang&password=1234");
        assertThat(uri.path()).isEqualTo("/login");
        assertThat(uri.queryStrings())
                .usingRecursiveComparison()
                .isEqualTo(Map.of("account", "mallang", "password", "1234"));
    }
}
