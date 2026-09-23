package com.techcourse.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceResolverTest {

    @Test
    @DisplayName("정적 리소스를 읽는다.")
    void read() {
        StaticResourceResolver resolver = new StaticResourceResolver();

        assertThat(resolver.read("/index.html"))
                .isPresent();
    }

    @Test
    @DisplayName("정적 리소스가 없으면 빈 값을 반환한다.")
    void readEmpty() {
        StaticResourceResolver resolver = new StaticResourceResolver();

        assertThat(resolver.read("/unknown.html"))
                .isEmpty();
    }
}
