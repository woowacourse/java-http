package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Http11ResourceFinderTest {

    private final Http11ResourceFinder resourceFinder = new Http11ResourceFinder();

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "index"})
    @DisplayName("찾을 수 있는 경우 리소스를 잘 찾는지 확인")
    void find(String requestUri) {
        Path path = resourceFinder.find(requestUri);

        assertThat(path).endsWith(Path.of("static", "index.html"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", ""})
    @DisplayName("기본 값인 경우 잘 찾는지 확인")
    void findDefault(String requestUri) {
        Path path = resourceFinder.find(requestUri);

        assertThat(path).endsWith(Path.of("static", "index.html"));
    }

    @Test
    @DisplayName("없는 자원인 경우 지정된 리로스로 인식하는지 확인")
    void findFail() {
        Path path = resourceFinder.find("gfgiu4idklfj");

        assertThat(path).endsWith(Path.of("static", "404.html"));
    }

    @Test
    @DisplayName("쿼리 스트링이 포함된 경우에도 리소스를 잘 찾는지 확인")
    void findWithQueriedRequestUri() {
        Path path = resourceFinder.find("/login?account=gugu&password=hard");

        assertThat(path).endsWith(Path.of("static", "login.html"));
    }
}
