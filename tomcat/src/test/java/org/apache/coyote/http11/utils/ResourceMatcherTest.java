package org.apache.coyote.http11.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceMatcherTest {

    @DisplayName("url이 '/'이면 '/hello.txt를 반환")
    @Test
    void returnDefaultResource() {
        String expected = "/hello.txt";
        String result = ResourceMatcher.matchName("/");

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("url에 확장자가 없다면, '.html'을 붙여 반환")
    @Test
    void returnHtmlAsExtension() {
        String expected = "/login.html";
        String result = ResourceMatcher.matchName("/login");

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("url이 '/'이 아니고 확장자도 있다면, 그대로 반환")
    @Test
    void returnInput() {
        String expected = "/index.html";
        String result = ResourceMatcher.matchName("/index.html");

        assertThat(result).isEqualTo(expected);
    }
}