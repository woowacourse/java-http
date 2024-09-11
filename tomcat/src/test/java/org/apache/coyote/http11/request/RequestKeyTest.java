package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RequestKey 테스트")
class RequestKeyTest {

    @DisplayName("Get 요청의 RequestKey를 생성한다.")
    @Test
    void toGet() {
        // given
        String path = "path";

        // when
        RequestKey requestKey = RequestKey.ofGet(path);

        // then
        assertAll(
                () -> assertThat(requestKey.path()).isEqualTo(path),
                () -> assertThat(requestKey.method()).isEqualTo(HttpMethod.GET)
        );
    }


    @DisplayName("Post 요청의 RequestKey를 생성한다.")
    @Test
    void toPost() {
        // given
        String path = "path";

        // when
        RequestKey requestKey = RequestKey.ofPost(path);

        // then
        assertAll(
                () -> assertThat(requestKey.path()).isEqualTo(path),
                () -> assertThat(requestKey.method()).isEqualTo(HttpMethod.POST)
        );
    }
}
