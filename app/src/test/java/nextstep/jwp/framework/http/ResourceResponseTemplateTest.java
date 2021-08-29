package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.template.ResourceResponseTemplate;
import nextstep.jwp.framework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceResponseTemplateTest {

    @Test
    @DisplayName("OK 상태코드와 리소스 반환 테스트")
    public void okTest() {

        final String resource = "/nextstep.txt";

        // when
        final HttpResponse response = new ResourceResponseTemplate().ok(resource);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.OK)
                                                  .contentType("text/plain;charset=utf-8")
                                                  .body(ResourceUtils.readString(resource))
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Unauthorized 상태코드와 리소스 반환 테스트")
    public void unauthorizedTest() {

        final String resource = "style.css";

        // when
        final HttpResponse response = new ResourceResponseTemplate().unauthorized(resource);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.UNAUTHORIZED)
                                                  .contentType("text/css;charset=utf-8")
                                                  .body(ResourceUtils.readString(resource))
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }
}
