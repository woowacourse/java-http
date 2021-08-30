package nextstep.jwp.framework.http.template;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class StringResponseTemplateTest {

    private static final String RESPONSE = "HELLO WORLD!";

    @Test
    @DisplayName("OK 상태코드와 리소스 반환 테스트")
    void okTest() {

        // when
        final HttpResponse response = new StringResponseTemplate().ok(RESPONSE);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.OK)
                                                  .contentType("text/plain;charset=utf-8")
                                                  .body(RESPONSE)
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }
}
