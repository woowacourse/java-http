package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MethodTest {

    @Test
    void isGet() {
        Method method = Method.GET;
        boolean isGet = method.isGet();

        assertThat(isGet).isTrue();
    }

    @Test
    void isPost() {
        Method method = Method.POST;
        boolean isPost = method.isPost();

        assertThat(isPost).isTrue();
    }
}
