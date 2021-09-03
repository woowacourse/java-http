package nextstep.jwp.framework.http.template;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.ContentType;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;
import nextstep.jwp.framework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceResponseTemplateTest {

    @Test
    @DisplayName("OK 상태코드와 리소스 반환 테스트")
    void okTest() {

        final String resource = "/nextstep.txt";

        // when
        final HttpResponse response = new ResourceResponseTemplate().ok(resource);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.OK)
                                                  .contentType(ContentType.PLAIN.getTypeWithUtf8())
                                                  .body(ResourceUtils.readString(resource))
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Unauthorized 상태코드와 리소스 반환 테스트")
    void unauthorizedTest() {

        final String resource = "style.css";

        // when
        final HttpResponse response = new ResourceResponseTemplate().unauthorized(resource);

        //then
        final HttpResponse expected = HttpResponse.status(HttpStatus.UNAUTHORIZED)
                                                  .contentType(ContentType.CSS.getTypeWithUtf8())
                                                  .body(ResourceUtils.readString(resource))
                                                  .build();

        assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }
}
