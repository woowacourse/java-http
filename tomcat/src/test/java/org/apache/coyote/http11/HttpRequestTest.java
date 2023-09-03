package org.apache.coyote.http11;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpRequestTest {

    @Test
    void Accept_key가_없으면_ConentType은_html이다() {
        // given
        List<String> request = List.of(
                "GET /login.html HTTP/1.1",
                "Host: localhost:8080/login?username=gugu&password=password",
                "Connection: keep-alive");

        // when
        HttpRequest httpRequest = HttpRequest.from(request);

        // then
        Assertions.assertThat(httpRequest.contentType()).isEqualTo(ContentType.HTML);
    }

    @Test
    void ContentType을_찾을_수_있다() {
        // given
        List<String> request = List.of(
                "GET /zipgo.js HTTP/1.1",
                "Host: zipgo.pet/",
                "Accept: text/javascript,*/*;q=0.1",
                "Connection: keep-alive");

        // when
        HttpRequest httpRequest = HttpRequest.from(request);

        // then
        Assertions.assertThat(httpRequest.contentType()).isEqualTo(ContentType.JAVASCRIPT);
    }

}