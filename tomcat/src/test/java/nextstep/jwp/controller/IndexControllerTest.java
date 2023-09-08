package nextstep.jwp.controller;

import static nextstep.jwp.FileFinder.getFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestLine;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class IndexControllerTest {

    private final IndexController indexController = new IndexController();

    @Test
    void 인덱스_페이지로_이동한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine(HttpMethod.GET, "/index.html", "version"),
            null,
            null,
            null
        );
        HttpResponse response = new HttpResponse();

        // when
        indexController.doGet(request, response);

        // then
        assertAll(
            () -> assertThat(response.getBody()).isEqualTo(getFileContent("/index.html")),
            () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK)
        );
    }
}
