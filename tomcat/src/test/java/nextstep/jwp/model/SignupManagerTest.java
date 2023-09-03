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
    void account_없이_회원가입을_할_경우_예외가_발생한다() {
        final RequestBody requestBody = RequestBody.from("password=wuga&email=gugu@naver.com");

        assertThatThrownBy(() -> SignupManager.singUp(requestBody))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void password_없이_회원가입을_할_없는_경우_예외가_발생한다() {
        final RequestBody requestBody = RequestBody.from("account=wuga&email=gugu@naver.com");

        assertThatThrownBy(() -> SignupManager.singUp(requestBody))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email_없이_회원가입을_할_경우_예외가_발생한다() {
        final RequestBody requestBody = RequestBody.from("account=wuga&password=wuga");

        assertThatThrownBy(() -> SignupManager.singUp(requestBody))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 잘못된_요청으로_회원가입을_할_경우_예외가_발생한다() {
        final RequestBody requestBody = RequestBody.from("");

        assertThatThrownBy(() -> SignupManager.singUp(requestBody))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public RequestBody getRequestBody() {
        return RequestBody.from("account=wuga&password=wuga&email=gugu@naver.com");
    }
}
