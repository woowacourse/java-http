package nextstep.jwp.framework.http.request;

import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.details.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @DisplayName("requestLine, requestHttpHeader, requestBody를 넘겨주면 HttpRequest를 생성한다.")
    @Test
    void parse() {
        String requestLine = "GET /hello?key1=value1&key2=value2 HTTP/1.1";
        Map<String, String> requestHttpHeader = new HashMap<>();
        requestHttpHeader.put("Content-Length", "9");
        String requestBody = "key3=value3";

        final HttpRequest httpRequest = HttpRequest.from(requestLine, requestHttpHeader, requestBody);

        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getRequestUrl().getUrl()).isEqualTo("/hello");
        assertThat(httpRequest.getRequestUrl().getQueryParam().searchValue("key1")).isEqualTo("value1");
        assertThat(httpRequest.getRequestUrl().getQueryParam().searchValue("key2")).isEqualTo("value2");
        assertThat(httpRequest.getProtocolVersion()).isEqualTo(ProtocolVersion.defaultVersion());
        assertThat(httpRequest.getRequestHttpHeader().getRequestHttpHeaderMap()).containsEntry("Content-Length", "9");
        assertThat(httpRequest.searchRequestBody("key3")).isEqualTo("value3");
    }
}
