package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpBody;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormDataTest {

    @Test
    @DisplayName("HttpBody로부터 FormData를 추출해낼 수 있다.")
    void createFormData() {
        //given
        String input = "account=gugu&password=gugupassword";
        HttpBody httpBody = HttpBody.from(input);

        //when
        FormData formData = FormData.from(httpBody);

        //then
        Assertions.assertThat(formData.get("account")).isEqualTo("gugu");
        Assertions.assertThat(formData.get("password")).isEqualTo("gugupassword");
    }

}