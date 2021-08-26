package nextstep.jwp.http.message.request;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.message.HeaderFields;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RequestHeaderTest {

    @DisplayName("RequestHeader를 생성한다.")
    @Test
    void create() {
        String headerString = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Host", "localhost:8080");
        linkedHashMap.put("Connection", "keep-alive");
        HeaderFields expectedHeaderFields = new HeaderFields(linkedHashMap);

        RequestHeader requestHeader = RequestHeader.from(headerString);
        assertThat(requestHeader.httpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestHeader.uri()).isEqualTo("/index.html");
        assertThat(requestHeader.httpVersion()).isEqualTo("HTTP/1.1");
        assertThat(requestHeader.getHeaderFields()).isEqualTo(expectedHeaderFields);
    }
}