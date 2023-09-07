package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestsTest {

    @Test
    void 메소드명과_요청_url_통해_관련_요청을_찾는다() throws IOException, URISyntaxException {
        HttpRequests httpRequest = HttpRequests.ofResourceNameAndMethod("/index.html", "GET");

        assertThat(httpRequest).isEqualTo(HttpRequests.INDEX);
    }

    @Test
    void 메소드명과_요청_url_이_존재하지_않으면_NOT_FOUND_반환() throws IOException, URISyntaxException {
        HttpRequests httpRequest = HttpRequests.ofResourceNameAndMethod("/noExists", "GET");

        assertThat(httpRequest).isEqualTo(HttpRequests.NOT_FOUND);
    }
}
