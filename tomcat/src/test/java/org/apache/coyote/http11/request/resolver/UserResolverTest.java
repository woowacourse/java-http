package org.apache.coyote.http11.request.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.model.User;
import org.apache.coyote.http11.request.RequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserResolverTest {

    @DisplayName("requestBody를 기반으로 유저를 만들 수 있다")
    @Test
    void resolve() {
        RequestBodyResolver<User> resolver = new UserResolver();
        String account = "testAccount";
        String password = "password";
        String email = "email@email.com";
        String requestBody = "account=" + account + "&password=" + password + "&email=" + email;

        User resolvedUser = resolver.resolve(new RequestBody(requestBody));

        assertThat(resolvedUser.getAccount()).isEqualTo(account);
    }
}
