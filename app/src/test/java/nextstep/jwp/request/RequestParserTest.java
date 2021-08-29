package nextstep.jwp.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class RequestParserTest {

    private String request = "POST /login HTTP/1.1\r\n" +
            "Host: localhost:8080\r\n" +
            "Connection: keep-alive\r\n" +
            "Content-Length: 30\r\n" +
            "Pragma: no-cache\r\n" +
            "Cache-Control: no-cache\r\n" +
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\"\r\n" +
            "sec-ch-ua-mobile: ?0\r\n" +
            "Upgrade-Insecure-Requests: 1\r\n" +
            "Origin: http://localhost:8080\r\n" +
            "Content-Type: application/x-www-form-urlencoded\r\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36\r\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\r\n" +
            "Sec-Fetch-Site: same-origin\r\n" +
            "Sec-Fetch-Mode: navigate\r\n" +
            "Sec-Fetch-User: ?1\r\n" +
            "Sec-Fetch-Dest: document\r\n" +
            "Referer: http://localhost:8080/login\r\n" +
            "Accept-Encoding: gzip, deflate, br\r\n" +
            "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\r\n" +
            "Cookie: Idea-2643bd33=f6d2b6b0-36fb-4c93-937d-3a44ba45f6ea\r\n" +
            "\r\n" +
            "account=gugu&password=password";

    @DisplayName("클라이언트의 요청을 파싱하여 ClientRequest 객체를 만들 수 있다")
    @Test
    void parse() throws IOException {
        final RequestParser requestParser = new RequestParser(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });
        final ClientRequest clientRequest = requestParser.parseClientRequest(request);

        assertThat(clientRequest).isEqualTo(ClientRequest.of(HttpMethod.POST, "/login"));
        assertThat(clientRequest.searchRequestBody("account")).isEqualTo("gugu");
        assertThat(clientRequest.searchRequestBody("password")).isEqualTo("password");
    }

}

