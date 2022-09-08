package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.http11.request.RequestBody;
import org.junit.jupiter.api.Test;

class QueryTest {

    @Test
    void queryMapping() {
        RequestBody body = RequestBody.from("account=gugu&password=password&email=hkkang%40woowahan.com".toCharArray());
        Query query = new Query(body);
        Map<String, String> mappedQuery = query.getMappedQuery();

        assertAll(
                () -> assertThat(mappedQuery.get("account")).isEqualTo("gugu"),
                () -> assertThat(mappedQuery.get("password")).isEqualTo("password"),
                () -> assertThat(mappedQuery.get("email")).isEqualTo("hkkang%40woowahan.com")
        );
    }
}