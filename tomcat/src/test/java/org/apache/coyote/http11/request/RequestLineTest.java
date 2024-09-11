package org.apache.coyote.http11.request;

import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.version.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    @DisplayName("문자열을 RequestLine 으로 조립한다.")
    void construct_request_line_with_string() {
        final RequestLine line = RequestLine.create("GET /static/img/header-background.png HTTP/1.1");
        assertThat(line.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(line.getPath()).isEqualTo(Path.from("/static/img/header-background.png"));
        assertThat(line.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    @DisplayName("문법에 맞지 않으면 예외를 발생한다.")
    void throw_exception_when_not_exist_method(){
        assertThatThrownBy(()->RequestLine.create("GET /static/img/header-background.png HTTP/1.1 SomeData"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
