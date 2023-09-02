package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.HttpExtensionType;
import org.apache.coyote.http11.HttpPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpPathTest {

    @DisplayName("queryParameter가 없는 경우")
    @Test
    void from_emptyQueryParameter() {
        // given
        final String uri = "/index.html";
        final String expectedResource = "/index";
        final HttpExtensionType expectedExtensionType = HttpExtensionType.HTML;

        // when
        final HttpPath httpPath = HttpPath.from(uri);

        // then
        assertAll(
                () -> assertThat(httpPath.getResource()).isEqualTo(expectedResource),
                () -> assertThat(httpPath.getContentType()).isEqualTo(expectedExtensionType),
                () -> assertThat(httpPath.getQueryParameter()).isEmpty()
        );
    }

    @DisplayName("queryParameter가 있는 경우")
    @Test
    void from_hasQueryParameter() {
        // given
        final String uri = "/login?account=gugu&password=password";
        final String expectedResource = "/login";
        final HttpExtensionType expectedExtensionType = HttpExtensionType.HTML;
        final Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("account", "gugu");
        expectedParams.put("password", "password");

        // when
        final HttpPath httpPath = HttpPath.from(uri);

        // then
        assertAll(
                () -> assertThat(httpPath.getResource()).isEqualTo(expectedResource),
                () -> assertThat(httpPath.getContentType()).isEqualTo(expectedExtensionType),
                () -> assertThat(httpPath.getQueryParameter()).isEqualTo(expectedParams)
        );
    }
}
