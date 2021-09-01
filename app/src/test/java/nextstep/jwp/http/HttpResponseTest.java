package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.message.HttpCookie;
import nextstep.jwp.http.message.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("쿠키를 추가하면 자동으로 헤더에 Set-Cookie가 추가된다.")
    @Test
    void addCookie_자동으로_헤더에도_추가() {
        // given
        String rawCookie = "JESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ";
        HttpCookie cookie = HttpCookie.parseFrom(rawCookie);

        // when
        HttpResponse httpResponse = new HttpResponseImpl();
        httpResponse.addCookie(cookie);

        // then
        HttpHeaders headers = httpResponse.getHeaders();
        assertThat(headers.getHeaderByName("Set-Cookie"))
            .get()
            .isEqualTo(rawCookie);
    }
}
