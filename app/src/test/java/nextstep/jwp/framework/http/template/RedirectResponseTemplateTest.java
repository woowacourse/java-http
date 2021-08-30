package nextstep.jwp.framework.http.template;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class RedirectResponseTemplateTest {

    @Test
    @DisplayName("Found 상태코드와 리소스 패스 반환 테스트")
    void foundTest() {

        // when
        final String location = "/nextstep.txt";
        final HttpResponse response = new RedirectResponseTemplate().found(location);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.FOUND)
                                                  .location(location)
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }
}
