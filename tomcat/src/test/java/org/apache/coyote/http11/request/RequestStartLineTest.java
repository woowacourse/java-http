package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

class RequestStartLineTest {

    @Test
    void startLine이_파싱되는지_확인한다() throws URISyntaxException {
        String startLine = "POST /login?account=gugu&password=password";
        RequestStartLine requestStartLine = RequestStartLine.from(startLine);

        assertAll(
                () -> assertThat(requestStartLine.isPost()).isTrue(),
                () -> assertThat(requestStartLine.getPath().getPath()).isEqualTo("/login"),
                () -> assertThat(requestStartLine.getPath().getRequestParams()).containsEntry("account", "gugu"),
                () -> assertThat(requestStartLine.getPath().getRequestParams()).containsEntry("password", "password")
        );
    }

    @Test
    void requestParams가_없는_경우_startLine이_파싱되는지_확인한다() throws URISyntaxException {
        String startLine = "POST /login";
        RequestStartLine requestStartLine = RequestStartLine.from(startLine);

        assertAll(
                () -> assertThat(requestStartLine.isPost()).isTrue(),
                () -> assertThat(requestStartLine.getPath().getPath()).isEqualTo("/login")
        );
    }
}
