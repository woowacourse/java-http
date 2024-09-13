package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void 폼_데이터를_반환() {
        // given
        String account = "ted";
        String password = "1234";
        String email = "ted%40gmail.com";
        String body = "account=" + account + "&password=" + password + "&email=" + email;
        RequestBody requestBody = new RequestBody(body);

        // when
        Map<String, String> actual = requestBody.getFormData();

        // then
        assertAll(
                () -> assertThat(actual).containsEntry("account", account),
                () -> assertThat(actual).containsEntry("password", password),
                () -> assertThat(actual).containsEntry("email", email)
        );
    }

    @Test
    void 폼_데이터가_없는_경우_빈_값을_반환() {
        // given
        RequestBody requestBody = new RequestBody(null);

        // when
        Map<String, String> actual = requestBody.getFormData();

        // then
        assertThat(actual).isEmpty();
    }

}
