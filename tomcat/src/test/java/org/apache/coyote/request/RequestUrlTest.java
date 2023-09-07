package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestUrlTest {

    @Test
    @DisplayName("쿼리 스트링이 없는 경우에도 RequestUrl을 생성할 수 있다.")
    void parse_url() {
        RequestUrl requestUrl = RequestUrl.from("index.html");

        assertThat(requestUrl.getPath()).isEqualTo("index.html");
    }

    @Test
    @DisplayName("쿼리 스트링이 있는 경우에는 QueryString을 Map형태로 파싱하여 RequestUrl을 생성할 수 있다.")
    void parse_url_queryString() {
        RequestUrl requestUrl = RequestUrl.from("login.html?account=kong&password=password");

        assertAll(
                () -> assertThat(requestUrl.getPath()).isEqualTo("login.html"),
                () -> assertThat(requestUrl.getQueryValue("account")).isEqualTo("kong"),
                () -> assertThat(requestUrl.getQueryValue("password")).isEqualTo("password")
        );


    }
}
