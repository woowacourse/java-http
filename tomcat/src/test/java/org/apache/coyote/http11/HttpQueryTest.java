package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.client.BadRequestException;
import org.apache.coyote.http11.query.HttpQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpQueryTest {

    @Nested
    class HTTP_쿼리 {

        @Test
        void URI로_생성할_수_있다() {
            // given & when
            HttpQuery httpQuery = HttpQuery.createByUri("/login?account=gugu&password=password");

            // then
            Assertions.assertAll(
                    () -> assertThat(httpQuery.findByKey("account")).isEqualTo("gugu"),
                    () -> assertThat(httpQuery.findByKey("password")).isEqualTo("password")
            );
        }

        @Test
        void 값이_존재하지_않으면_에러가_발생한다() {
            // when & then
            assertThatThrownBy(() -> HttpQuery.createByUri("/login?accoun="))
                    .isExactlyInstanceOf(BadRequestException.class);
        }

        @Test
        void 키가_존재하지_않으면_에러가_발생한다() {
            // when & then
            assertThatThrownBy(() -> HttpQuery.createByUri("/login?=tacan"))
                    .isExactlyInstanceOf(BadRequestException.class);
        }

        @Test
        void 키와_값이_존재하지_않으면_에러가_발생한다() {
            // when & then
            assertThatThrownBy(() -> HttpQuery.createByUri("/login?"))
                    .isExactlyInstanceOf(BadRequestException.class);
        }
    }
}
