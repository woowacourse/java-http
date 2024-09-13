package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestURITest {

    @Test
    @DisplayName("path와 extension이 모두 있는 경우 path와 extension을 반환한다.")
    void testGetPathWithExtension() {
        RequestURI requestURI = RequestURIFactory.create("https://example.com/login.html?key=value");

        String pathWithExtension = requestURI.getPathWithExtension();

        assertThat(pathWithExtension).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("extension이 없는 경우 path만 반환한다.")
    void testGetPathWithExtensionWithoutExtension() {
        RequestURI requestURI = RequestURIFactory.create("https://example.com/login?key=value");

        String pathWithExtension = requestURI.getPathWithExtension();

        assertThat(pathWithExtension).isEqualTo("/login");
    }

}
