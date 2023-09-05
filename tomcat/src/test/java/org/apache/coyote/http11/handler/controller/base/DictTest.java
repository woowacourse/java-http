package org.apache.coyote.http11.handler.controller.base;

import nextstep.jwp.controller.base.Dict;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.request.uri.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class DictTest {

    @DisplayName("path와 HttpMethod를 이용해서 Enum을 가져온다")
    @Test
    void returns_enum_using_path_and_http_method() {
        // given
        String path = "/index";
        HttpMethod httpMethod = HttpMethod.GET;

        // when
        Dict dict = Dict.find(path, httpMethod);

        // then
        assertThat(dict).isEqualTo(Dict.INDEX_GET);
    }

    @DisplayName("path혹은 HttpMethod가 잘못된다면 예외를 발생한다")
    @Test
    void throws_exception_when_path_or_http_method_invalid() {
        // given
        String path = "/index";
        String invalidPath = "/index-invalid";

        HttpMethod httpMethod = HttpMethod.GET;
        HttpMethod invalidHttpMethod = HttpMethod.POST;


        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> Dict.find(path, invalidHttpMethod))
                    .isInstanceOf(NotFoundException.class);

            softly.assertThatThrownBy(() -> Dict.find(invalidPath, httpMethod))
                    .isInstanceOf(NotFoundException.class);
        });
    }
}
