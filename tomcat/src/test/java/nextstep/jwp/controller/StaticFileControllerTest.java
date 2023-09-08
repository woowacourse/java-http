package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestLine;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class StaticFileControllerTest {

    private final StaticFileController staticFileController = new StaticFileController();

    @Test
    void html_요청이_오면_content_type은_html() throws Exception {
        // given
        HttpRequest request = createGetRequestByPath("/index.html");
        HttpResponse actual = new HttpResponse();

        // when
        staticFileController.doGet(request, actual);
        // then
        assertThat(actual.getHeaders().get("Content-Type")).contains("text/html");
    }

    @Test
    void css_요청이_오면_content_type은_css() throws Exception {
        HttpRequest request = createGetRequestByPath("/css/styles.css");
        HttpResponse actual = new HttpResponse();

        // when
        staticFileController.doGet(request, actual);
        // then
        assertThat(actual.getHeaders().get("Content-Type")).contains("text/css");
    }

    @Test
    void js_요청이_오면_content_type은_js() throws Exception {
        // given
        HttpRequest request = createGetRequestByPath("/js/scripts.js");
        HttpResponse actual = new HttpResponse();

        // when
        staticFileController.doGet(request, actual);
        // then
        assertThat(actual.getHeaders().get("Content-Type")).contains("application/javascript");
    }

    private HttpRequest createGetRequestByPath(String path) {
        return new HttpRequest(
            new HttpRequestLine(HttpMethod.GET, path, "version"),
            null,
            null,
            null
        );
    }
}
