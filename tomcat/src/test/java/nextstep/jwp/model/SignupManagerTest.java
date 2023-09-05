package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.coyote.http11.RequestBody;
import org.junit.jupiter.api.Test;

class SignupManagerTest {

    @Test
    void 정상적인_요청일때_회원가입을_할_수_있다() {
        final RequestBody requestBody = RequestBody.from("account=wuga&password=wuga&email=gugu@naver.com");

        assertDoesNotThrow(() -> SignupManager.singUp(requestBody));
    }

    @Test
    void 잘못된_요청으로_회원가입을_할_경우_예외가_발생한다() {
        final RequestBody emptyBody = RequestBody.from("");
        final RequestBody notExistAccountBody = RequestBody.from("password=wuga&email=gugu@naver.com");
        final RequestBody notExistPasswordBody = RequestBody.from("account=wuga&email=gugu@naver.com");
        final RequestBody notExistEmailPasswordBody = RequestBody.from("account=wuga&password=wuga");

        assertThatThrownBy(() -> SignupManager.singUp(emptyBody))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SignupManager.singUp(notExistAccountBody))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SignupManager.singUp(notExistPasswordBody))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SignupManager.singUp(notExistEmailPasswordBody))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
