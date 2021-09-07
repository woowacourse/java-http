package nextstep.jwp.request;

import nextstep.jwp.response.CharlieHttpResponse;
import nextstep.jwp.web.model.Cookie;
import nextstep.jwp.web.model.HttpCookie;
import nextstep.jwp.web.model.HttpSession;
import nextstep.jwp.web.model.HttpSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CharlieHttpRequestTest {

    String request = "GET /index HTTP/1.1\r\n" +
            "HOST: localhost:8080\r\n" +
            "Cookie: a=aaa; b=bbb; c=ccc\r\n" +
            "Accept-Language: kr\r\n";


    @DisplayName("getCookies() : HttpRequest에서 모든 쿠키들을 가져온다.")
    @Test
    void getCookies() {
        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            CharlieHttpRequest request = CharlieHttpRequest.of(bufferedReader);
            HttpCookie cookies = request.getCookies();
            Map<String, Cookie> values = cookies.getValues();

            assertThat(values).containsKeys("a", "b", "c");
            assertThat(values).containsValues(new Cookie("a", "aaa"), new Cookie("b", "bbb"), new Cookie("c", "ccc"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("getCookies() : HttpRequest의 헤더에 쿠키가 존재하지 않는다면 HttpCookie.EMPTY_HTTP_COOKIE 객체를 반환한다.")
    @Test
    void getCookiesWithoutCookie() {
        String request = "GET /index HTTP/1.1\r\n" +
                "HOST: localhost:8080\r\n" +
                "Accept-Language: kr\r\n";

        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            CharlieHttpRequest httpRequest = CharlieHttpRequest.of(bufferedReader);
            HttpCookie cookies = httpRequest.getCookies();

            assertThat(cookies).isEqualTo(HttpCookie.emptyCookies());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("getSession() : 헤더에 있는 쿠키중 JSESSIONID를 확인해서 존재하면 세션 객체를 가져온다.")
    @Test
    void getSession() {
        HttpSessions.save(new HttpSession("aaa"));

        String request = "GET /index HTTP/1.1\r\n" +
                "HOST: localhost:8080\r\n" +
                "Cookie: JSESSIONID=aaa; b=bbb; c=ccc\r\n" +
                "Accept-Language: kr\r\n";

        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            CharlieHttpRequest httpRequest = CharlieHttpRequest.of(bufferedReader);
            CharlieHttpResponse httpResponse = new CharlieHttpResponse();
            HttpSession session = httpRequest.getSession(httpResponse);

            assertThat(session).isNotNull();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("getSession() : 헤더에 있는 쿠키중 JSESSIONID를 확인해서 존재하지 않으면 새로운 HttpSession 객체를 생성한다.")
    @Test
    void getSessionWithoutJSESSIONIDCookie() {
        String request = "GET /index HTTP/1.1\r\n" +
                "HOST: localhost:8080\r\n" +
                "Accept-Language: kr\r\n";

        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            CharlieHttpRequest httpRequest = CharlieHttpRequest.of(bufferedReader);
            CharlieHttpResponse httpResponse = new CharlieHttpResponse();
            HttpSession session = httpRequest.getSession(httpResponse);

            assertThat(session).isNotNull();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}