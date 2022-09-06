package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class RequestStartLineTest {

    @Test
    void startLine이_파싱되는지_확인한다() {
        String startLine = "POST /login?account=gugu&password=password";
        RequestStartLine requestStartLine = RequestStartLine.from(startLine);

        assertAll(
                () -> assertThat(requestStartLine.isPost()).isTrue(),
                () -> assertThat(requestStartLine.getPath().getUrl()).isEqualTo("/login"),
                () -> assertThat(requestStartLine.getPath().getRequestParams()).containsEntry("account", "gugu"),
                () -> assertThat(requestStartLine.getPath().getRequestParams()).containsEntry("password", "password")
        );
    }
}
