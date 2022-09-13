package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.request.RequestBody;
import org.junit.jupiter.api.Test;

class QueryParametersTest {

    @Test
    void queryMapping() {
        RequestBody body = RequestBody.from("account=gugu&password=password&email=hkkang%40woowahan.com".toCharArray());
        QueryParameters queryParameters = new QueryParameters(body);

        assertAll(
                () -> assertThat(queryParameters.find("account")).isEqualTo("gugu"),
                () -> assertThat(queryParameters.find("password")).isEqualTo("password"),
                () -> assertThat(queryParameters.find("email")).isEqualTo("hkkang%40woowahan.com")
        );
    }
}
