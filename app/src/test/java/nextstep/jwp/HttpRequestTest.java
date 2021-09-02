package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import nextstep.jwp.http.cookie.HttpCookie;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.request.RequestHeader;
import nextstep.jwp.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @Test
    @DisplayName("쿠키값이 존재할시 JSESSIONID 를 추출한다.")
    void notFound() {
        //given
        RequestLine requestLine = RequestLine.createFromPlainText("GET /index.html HTTP/1.1 ");
        RequestHeader requestHeader = new RequestHeader(new HashMap<>() {{
            put("Host", "localhost:8080 ");
            put("Connection", "keep-alive ");
            put("Cookie",
                    "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ");

        }}, HttpCookie.StringOf(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 "));
        RequestBody requestBody = new RequestBody("");
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader, requestBody);
        // when

        // then
        assertThat(httpRequest.getCookieSessionId()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
