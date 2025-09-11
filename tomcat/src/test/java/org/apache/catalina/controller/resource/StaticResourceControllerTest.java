package org.apache.catalina.controller.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StaticResourceControllerTest {

    private StaticResourceController staticResourceController;
    private Method readStaticResourceMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        staticResourceController = new StaticResourceController();
        readStaticResourceMethod = StaticResourceController.class.getDeclaredMethod("readStaticResource", String.class);
        readStaticResourceMethod.setAccessible(true);
    }

    @DisplayName("안전하지 않은 경로로 리소스를 요청하면 빈 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "/../index.html",
            "/..\\index.html",
            "/css/../../index.html"
    })
    void readStaticResource_withUnsafePath(final String unsafePath) throws Exception {
        // when
        @SuppressWarnings("unchecked")
        final Optional<byte[]> result = (Optional<byte[]>) readStaticResourceMethod.invoke(staticResourceController, unsafePath);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("정상적인 경로로 리소스를 요청하면 리소스 내용을 반환한다.")
    @Test
    void readStaticResource_withSafePath() throws Exception {
        // given
        final String safePath = "/index.html";

        // when
        @SuppressWarnings("unchecked")
        final Optional<byte[]> result = (Optional<byte[]>) readStaticResourceMethod.invoke(staticResourceController, safePath);

        // then
        assertThat(result).isNotEmpty();
    }

    @DisplayName("정규화하면 안전해지는 경로로 리소스를 요청하면 리소스 내용을 반환한다.")
    @Test
    void readStaticResource_withNormalizablePath() throws Exception {
        // given
        final String path = "/css/../index.html"; // should normalize to /index.html

        // when
        @SuppressWarnings("unchecked")
        final Optional<byte[]> result = (Optional<byte[]>) readStaticResourceMethod.invoke(staticResourceController, path);

        // then
        assertThat(result).isNotEmpty();
    }
}
