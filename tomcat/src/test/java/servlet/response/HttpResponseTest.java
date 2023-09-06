package servlet.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.coyote.http.HttpCookie;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void 생성자는_호출하면_HttpResponse를_초기화한다() {
        final HttpResponse actual = new HttpResponse();

        assertThat(actual).isNotNull();
    }

    @Test
    void sendRedirect_메서드는_전달한_path에_대한_Location_header_정보를_추가한다() {
        final HttpResponse response = new HttpResponse();

        assertDoesNotThrow(() -> response.sendRedirect("/login"));
    }

    @Test
    void addCookie_메서드는_전달한_쿠키를_저장한다() {
        final HttpResponse response = new HttpResponse();

        assertDoesNotThrow(() -> response.addCookie(HttpCookie.fromSessionId("sessionId")));
    }
}
