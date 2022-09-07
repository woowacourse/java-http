package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11QueryParamsTest {

    @DisplayName("key에 해당하는 value를 반환한다.")
    @Test
    void getValueFromKey() {
        final Http11QueryParams queryParams = Http11QueryParams.from("/login?account=gugu&password=password");

        final String account = queryParams.getValueFrom("account");

        assertThat(account).isEqualTo("gugu");
    }
}
