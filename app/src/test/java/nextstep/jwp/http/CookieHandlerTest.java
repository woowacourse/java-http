package nextstep.jwp.http;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nextstep.jwp.Fixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieHandlerTest {

    @Test
    @DisplayName("Set-Cookie 설정")
    void setCookie() {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login");
        HttpResponse httpResponse = HttpResponse.empty();

        CookieHandler.handle(httpRequest, httpResponse);
        assertTrue(httpResponse.containsHeader("Set-Cookie"));
    }

    @Test
    @DisplayName("존재하지 않는 Set-Cookie 설정")
    void notSetCookie() {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login");
        httpRequest.httpCookie().addCookie("JSESSIONID", "new");
        HttpResponse httpResponse = HttpResponse.empty();

        CookieHandler.handle(httpRequest, httpResponse);
        assertFalse(httpResponse.containsHeader("Set-Cookie"));
    }
}
