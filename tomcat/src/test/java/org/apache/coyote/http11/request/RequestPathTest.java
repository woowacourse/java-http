package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestPathTest {

    @Test
    void parsePath() {
        // given
        String pathString = "/hello";
        // when
        RequestPath requestPath = RequestPath.parse(pathString);
        // then
        Assertions.assertAll(
                () -> assertThat(requestPath.getPath()).isEqualTo("/hello"),
                () -> assertThat(requestPath.getQueryParameters()).isEmpty()
        );
    }

    @Test
    void parsePathWithQueryString () {
        // given
        String pathString = "/hello?a=1";
        // when
        RequestPath requestPath = RequestPath.parse(pathString);
        // then
        Assertions.assertAll(
                () -> assertThat(requestPath.getPath()).isEqualTo("/hello"),
                () -> assertThat(requestPath.getQueryParameters()).contains(Map.entry("a", "1"))
        );
    }
    
    @Test
    void parsePathWithMutlipleQueryString() {
        // given
        String pathString = "/hello?a=1&b=2";
        // when
        RequestPath requestPath = RequestPath.parse(pathString);
        // then
        Assertions.assertAll(
                () -> assertThat(requestPath.getPath()).isEqualTo("/hello"),
                () -> assertThat(requestPath.getQueryParameters()).contains(Map.entry("a", "1"))
        );
    }
}
