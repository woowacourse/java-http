package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestUrlTest {

    @Test
    @DisplayName("Request URL을 입력받아 Path와 QueryParams를 생성한다")
    void parseUrl() {
        String url = "/login?account=gugu&password=password";

        RequestUrl requestUrl = RequestUrl.from(url);
        String path = requestUrl.getPath();
        QueryParams queryParams = requestUrl.getQueryParams();

        assertAll(
                () -> assertThat(path).isEqualTo("/login"),
                () -> assertThat(queryParams.size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("쿼리 스트링이 없다면 빈 QueryParams 객체를 생성한다")
    void emptyQueryString() {
        String url = "/";
        RequestUrl requestUrl = RequestUrl.from(url);

        QueryParams queryParams = requestUrl.getQueryParams();

        assertAll(
                () -> assertThat(queryParams.hasParams()).isFalse()
        );
    }
}
