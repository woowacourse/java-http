package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class WelcomeControllerTest {

    @Test
    void 요청_URI가_비어있다면_WelcomePage로_이동한다() {
        final var result = TestHttpUtils.sendGet("http://127.0.0.1:8080", "/");

        assertThat(result.body()).isEqualTo("Hello world!");
        assertThat(result.statusCode()).isEqualTo(200);
    }
}
