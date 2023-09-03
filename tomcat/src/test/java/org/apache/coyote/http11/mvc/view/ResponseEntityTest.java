package org.apache.coyote.http11.mvc.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ResponseEntity 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseEntityTest {

    @Nested
    class ResponseEntity_객체_생성_테스트 {
        @Test
        void 단순_문자열_데이터로_생성() {
            // given
            final String data = "Hello World!";

            // when
            final ResponseEntity responseEntity = ResponseEntity.fromSimpleStringData(data);

            // then
            assertThat(responseEntity.getView()).isInstanceOf(SimpleStringDataView.class);
            assertThat(responseEntity.getView().getContentType()).isEqualTo("text/plain;charset=utf-8");
        }

        @Test
        void ViewResource로_생성() {
            // given
            final String uri = "/index.html";

            // when
            final ResponseEntity responseEntity = ResponseEntity.forwardTo(uri);

            // then
            assertThat(responseEntity.getView()).isInstanceOf(StaticResourceView.class);
            assertThat(responseEntity.getHttpStatus()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void 리다이렉트로_생성() {
            // given
            final String uri = "/index.html";

            // when
            final ResponseEntity responseEntity = ResponseEntity.redirectTo(uri);

            // then
            assertThat(responseEntity.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(responseEntity.getHeaders().get("Location")).isEqualTo(uri);
        }

        @Test
        void 존재_하지_않는_자원으로_생성시_404_상태를_가진다() {
            // given
            final String uri = "/not_found.html";

            // when
            final ResponseEntity responseEntity = ResponseEntity.forwardTo(uri);

            // then
            assertThat(responseEntity.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
