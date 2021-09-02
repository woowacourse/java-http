package nextstep.learning.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class StringTest {

    @DisplayName("클라이언트 request의 첫 줄을 유의미하게 자를 수 있다")
    @Test
    void request() {
        String clientRequest = "GET /index.html HTTP/1.1";
        List<String> expected = Arrays.asList("GET", "index.html", "HTTP/1.1");

        final int spaceAndSlashIndex = clientRequest.indexOf(" /");
        final int lastSpace = clientRequest.lastIndexOf(" ");

        List<String> actual = new ArrayList<>();
        actual.add(clientRequest.substring(0, spaceAndSlashIndex));
        actual.add(clientRequest.substring(spaceAndSlashIndex + 2, lastSpace));
        actual.add(clientRequest.substring(lastSpace + 1));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("요청의 쿼리 스트링을 파싱할 수 있다.")
    @Test
    void queryStringParsing() {
        String queryString = "login?account=gugu&password=password";
        Map<String, String> expected = new HashMap<>();
        expected.put("account", "gugu");
        expected.put("password", "password");

        Map<String, String> actual = new HashMap<>();
        final String queries = queryString.substring(queryString.lastIndexOf("?") + 1);
        final String[] splited = queries.split("&");
        for (String s : splited) {
            final String[] split = s.split("=");
            actual.put(split[0], split[1]);
        }

        assertThat(expected).isEqualTo(actual);
    }

    @DisplayName("정규표현식으로 '\\' 를 '/' 로 변경할 수 있다.")
    @Test
    void convertDoubleBackSlash() {
        String toBeChanged = "G:\\foo:\\bar";
        String changed = toBeChanged.replaceAll("\\\\+", "/");

        assertThat(changed).isEqualTo("G:/foo:/bar");
    }
}
