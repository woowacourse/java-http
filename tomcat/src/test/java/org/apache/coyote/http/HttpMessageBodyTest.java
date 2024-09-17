package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Http 바디 메세지 테스트")
class HttpMessageBodyTest {

    private HttpMessageBody httpMessageBody;

    @BeforeEach
    void setUp() {
        httpMessageBody = new HttpMessageBody("username=John&password=1234");
    }

    @Test
    @DisplayName("비어 있는 body를 생성한다.")
    void createEmptyBody() {
        // given
        HttpMessageBody emptyBody = HttpMessageBody.createEmptyBody();

        // when & then
        assertThat(emptyBody.resolveBodyMessage()).isEmpty();
    }

    @Test
    @DisplayName("메시지를 작성하고 body에 반영한다.")
    void writeMessage() {
        // given
        httpMessageBody.write("newBodyMessage");

        // when & then
        assertThat(httpMessageBody.resolveBodyMessage()).isEqualTo("newBodyMessage");
    }

    @Test
    @DisplayName("form-data에서 특정 값을 가져올 수 있다.")
    void getFormData() {
        Assertions.assertAll(
                () -> assertThat(httpMessageBody.getFormData("username")).isEqualTo("John"),
                () -> assertThat(httpMessageBody.getFormData("password")).isEqualTo("1234")
        );
    }

    @Test
    @DisplayName("form-data에 없는 값을 요청할 때 예외를 발생시킨다.")
    void getFormDataThrowsExceptionWhenKeyNotFound() {
        assertThatThrownBy(() -> httpMessageBody.getFormData("email"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessageContaining("email에 해당하는 form-data를 찾지 못했습니다");
    }

    @Test
    @DisplayName("올바르지 않은 형식의 form-data를 요청할 때 예외를 발생시킨다.")
    void getFormDataThrowsExceptionWhenInvalidFormat() {
        httpMessageBody.write("invalidDataWithoutEquals");
        assertThatThrownBy(() -> httpMessageBody.getFormData("username"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessageContaining("요청의 form-data 형식이 올바르지 않습니다");
    }
}
