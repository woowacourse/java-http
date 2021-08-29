package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.template.StringResponseTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class StringResponseTemplateTest {

    private static final String RESPONSE = "HELLO WORLD!";

    @Test
    @DisplayName("OK 상태코드와 리소스 반환 테스트")
    public void okTest() {

        // when
        final HttpResponse response = new StringResponseTemplate().ok(RESPONSE);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.OK)
                                                  .contentType("text/plain;charset=utf-8")
                                                  .body(RESPONSE)
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Found 상태코드와 리소스 패스 반환 테스트")
    public void foundTest() {

        // when
        final HttpResponse response = new StringResponseTemplate().found(RESPONSE);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.FOUND)
                                                  .location(RESPONSE)
                                                  .contentType("text/plain;charset=utf-8")
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }
}
