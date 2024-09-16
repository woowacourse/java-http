package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTargetTest {

    @DisplayName("http 타겟을 입력받아 쿼리 파라미터가 존재하면 경로와 쿼리파라미터를 갖고 생성된다.")
    @Test
    void createWithPathAndQueryParameter() {
        HttpRequestTarget httpRequestTarget = HttpRequestTarget.from("/login?name=kaki");

        String path = httpRequestTarget.getPath();
        QueryParameters queryParameters = httpRequestTarget.getQueryParameters();

        assertAll(
                () -> assertThat(path).isEqualTo("/login"),
                () -> assertThat(queryParameters.getValueBy("name")).isEqualTo("kaki")
        );
    }

    @DisplayName("http 타겟을 입력받아 쿼리 파라미터가 존재하지 않으면 경로와 빈 쿼리파라미터를 갖고 생성된다.")
    @Test
    void createWithPathAndEmptyQueryParameter() {
        HttpRequestTarget httpRequestTarget = HttpRequestTarget.from("/login");

        String path = httpRequestTarget.getPath();
        QueryParameters queryParameters = httpRequestTarget.getQueryParameters();

        assertAll(
                () -> assertThat(path).isEqualTo("/login"),
                () -> assertThat(queryParameters.hasParameters()).isFalse()
        );
    }
}
