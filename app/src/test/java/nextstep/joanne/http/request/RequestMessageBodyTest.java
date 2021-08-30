package nextstep.joanne.http.request;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMessageBodyTest {

    @Test
    void valueFromKey() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("bookId", "1234");

        RequestMessageBody requestMessageBody = new RequestMessageBody(hashMap);
        assertThat(requestMessageBody.valueFromKey("bookId")).isEqualTo("1234");
    }
}