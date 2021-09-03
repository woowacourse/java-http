package nextstep.jwp.httpserver.domain.view;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.domain.response.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("View 도메인 테스트")
class ViewTest {

    @Test
    @DisplayName("존재하는 html 읽어오기")
    void render() throws URISyntaxException, IOException {
        // given
        View view = new View("static/index.html");
        HttpResponse httpResponse = new HttpResponse();

        // when
        view.render(httpResponse);

        // then
        assertThat(httpResponse.getBody()).isNotNull();
    }

    @Test
    @DisplayName("존재하지않는 html 읽어오기")
    void renderNotExist() throws URISyntaxException, IOException {
        // given
        View view = new View("static/101.html");
        HttpResponse httpResponse = new HttpResponse();

        // when
        view.render(httpResponse);

        // then
        assertThat(httpResponse.getBody()).contains("404 Error");
    }
}
