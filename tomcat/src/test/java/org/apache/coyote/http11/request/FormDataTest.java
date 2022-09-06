package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormDataTest {

    @DisplayName("쿼리스트링을 받아 파싱하여 반환한다.")
    @Test
    void getValue() {
        FormData formData = FormData.from("account=gugu&password=password");

        Map<String, String> values = formData.getValues();
        String accountValue = values.get("account");
        String passwordValue = values.get("password");

        assertAll(
                () -> assertThat(accountValue).isEqualTo("gugu"),
                () -> assertThat(passwordValue).isEqualTo("password")
        );
    }
}