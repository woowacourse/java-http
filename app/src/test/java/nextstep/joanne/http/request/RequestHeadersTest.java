package nextstep.joanne.http.request;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeadersTest {

    @Test
    void containsResource() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Accept", "text/css");

        RequestHeaders requestHeaders = new RequestHeaders(hashMap);
        assertThat(requestHeaders.containsResource("css")).isTrue();
    }

    @Test
    void containsResourceFalse() {
        HashMap<String, String> hashMap = new HashMap<>();

        RequestHeaders requestHeaders = new RequestHeaders(hashMap);
        assertThat(requestHeaders.containsResource("css")).isFalse();
    }
}