package org.apache.catalina.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Optional;

import org.apache.catalina.auth.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponsePageTest {

    @Test
    @DisplayName("성공 : 로그인이 안됐을 때 url에 맞는 ResponsePage을 알아낼 수 있다.")
    void fromUrlSuccessByIsNotLogin() {
        ResponsePage expected = ResponsePage.LOGIN_IS_LOGIN;

        Optional<ResponsePage> actual = ResponsePage.fromUrl(expected.getUrl(), new HttpCookie(new HashMap<>()));

        assertThat(actual).isEqualTo(Optional.of(expected));
    }

    @Test
    @DisplayName("성공 : url이 와 연관된 값이 없으면 optional을 반환한다.")
    void fromUrlSuccessByNotContaionUrl() {
        Optional<ResponsePage> actual = ResponsePage.fromUrl("/a", new HttpCookie(new HashMap<>()));

        assertThat(actual).isEmpty();
    }
}
