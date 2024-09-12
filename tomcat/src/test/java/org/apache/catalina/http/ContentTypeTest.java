package org.apache.catalina.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ContentTypeTest {
    @Nested
    @DisplayName("콘텐츠 파일 타입 조회")
    class of {
        @Test
        @DisplayName("성공 : 문자열 가지고 콘텐츠 파일 조회")
        void getFileTypeSuccess() {
            ContentType actual = ContentType.of("text/css");

            assertThat(actual).isEqualTo(ContentType.CSS);
        }

        @Test
        @DisplayName("성공 : 문자열 잘못된 값이면 text/html을 기본 값으로 설정")
        void getFileTypeSuccessByNotContainAccept() {
            ContentType actual = ContentType.of("");

            assertThat(actual).isEqualTo(ContentType.HTML);
        }
    }
}
