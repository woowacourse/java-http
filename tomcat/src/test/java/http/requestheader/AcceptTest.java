package http.requestheader;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AcceptTest {

    @DisplayName("Accept 헤더가 존재하지 않을 경우 기본 Content-type의 Media-type은 text/html이다.")
    @Test
    void should_text_html_when_no_accept_header() throws URISyntaxException {
        // given
        Accept accept = new Accept();
        String expected = "text/html";

        // when
        String actual = accept.processContentType(new URI("/test/path"));

        // then
        assertThat(actual).isEqualTo(expected);
    }

}