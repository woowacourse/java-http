package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AcceptanceTest {

    public static final int KEY_ON_KEY_VALUE_FORMAT = 0;
    public static final int VALUE_ON_KEY_VALUE_FORMAT = 1;
    public static final int STATUS_LINE = 0;

    @DisplayName("미션 첫 번째 요구사항")
    @Test
    void getIndex() {
        //given
        final String httpRequest = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(getCode(entire)).isEqualTo("200");
    }

    @DisplayName("미션 두 번째 요구 사항")
    @ParameterizedTest
    @MethodSource
    void secondAndThirdMission(String httpRequest, String code) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(getCode(entire)).isEqualTo(code);
    }

    static Stream<Arguments> secondAndThirdMission() {
        return Stream.of(
            Arguments.of(String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 53",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hybeom@gmail.com"), "302"),
            Arguments.of(String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 53",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=younge&password=password&email=hybeom@gmail.com"), "401")
        );
    }

    @ParameterizedTest
    @MethodSource
    void fourthMission(String httpRequest, String code) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(getCode(entire)).isEqualTo(code);
    }

    static Stream<Arguments> fourthMission() {
        return Stream.of(
            Arguments.of(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 53",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=mungto&password=password&email=hybeom@gmail.com"), "302"),
            Arguments.of(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 53",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=gugu@gmail.com"), "401")
        );
    }

    @DisplayName("미션 5 번째 요구사항")
    @Test
    void staticSuccess() {
        //given
        final String httpRequest = String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/css,*/*;q=0.1",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(getCode(entire)).isEqualTo("200");
    }

    private String getCode(String entireResponse) {
        String[] lines = entireResponse.split("\n");
        String[] firstHeaderLine = lines[STATUS_LINE].split(" ");
        return firstHeaderLine[1];
    }

    @DisplayName("미션 6번째 요구사항, 로그인 시 JSessionID 반환")
    @Test
    void loginSuccessWithToken() {
        //given
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 53",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hybeom@gmail.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String entire = socket.output();
        final Map<String, String> headers = extractHeaderLines(entire);
        assertThat(headers).containsKey("Set-Cookie");
        assertThat(getCode(entire)).isEqualTo("302");
    }

    @DisplayName("미션 7번째 요구사항 - 1, 세션으로 로그인")
    @Test
    void loginWithSession() {
        //given
        String JSessionId = getJsessionWithLogin();

        //when
        HttpSession httpSession = HttpSessions.getSession(JSessionId);
        String userName = (String) httpSession.getAttribute("user");

        // then
        assertThat(httpSession).isNotNull();
        assertThat(userName).isEqualTo("gugu");
    }

    @DisplayName("미션 7번째 요구사항 - 2, 로그인 되있음에도 index로 로그인 시도 ")
    @Test
    void connectIndexWithLoginSession() {
        //given
        String JSessionId = getJsessionWithLogin();
        final String httpRequest = String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 53",
            "Content-Type: application/x-www-form-urlencoded",
            "Cookie: JSESSIONID="+JSessionId,
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hybeom@gmail.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        //when
        requestHandler.run();
        String entire = socket.output();
        final Map<String, String> headers = extractHeaderLines(entire);

        // then
        assertThat(getCode(entire)).isEqualTo("302");
        String location = headers.get("Location");
        assertThat(location).isEqualTo("/index");
    }

    private String getJsessionWithLogin() {
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 53",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hybeom@gmail.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        final String entire = socket.output();

        final Map<String, String> headers = extractHeaderLines(entire);
        final String cookie = headers.get("Set-Cookie");
        final String[] parsedCookie = cookie.split("=");
        final String rawJSessionId = parsedCookie[VALUE_ON_KEY_VALUE_FORMAT];
        final String JSessionId = rawJSessionId.replaceAll("\r","");
        return JSessionId;
    }

    private Map<String, String> extractHeaderLines(String rawResponse) {
        Map<String, String> headers = new HashMap<>();
        String[] response = rawResponse.split("\n");
        for (int i = 1; i < response.length; i++) {
            final String rawHeader = response[i];
            if ("\r".equals(rawHeader)) {
                break;
            }
            String[] header = rawHeader.split(": ");
            headers.put(header[KEY_ON_KEY_VALUE_FORMAT], header[VALUE_ON_KEY_VALUE_FORMAT].replaceAll("\r",""));
        }
        return headers;
    }
}
