package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookiesTest {

    @Test
    @DisplayName("쿠키에 값을 저장하고 꺼낸다.")
    void saveAndFind() {
        final Cookies cookies = Cookies.from("name=alex; age=17");

        final String findName = cookies.getValue("name")
                .get();
        final String findAge = cookies.getValue("age")
                .get();

        assertAll(
                () -> assertThat(findName).isEqualTo("alex"),
                () -> assertThat(findAge).isEqualTo("17")
        );
    }
}
