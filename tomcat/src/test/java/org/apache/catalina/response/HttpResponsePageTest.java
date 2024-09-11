package org.apache.catalina.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Optional;

import org.apache.catalina.auth.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponsePageTest {

    @Test
    @DisplayName("성공 : 로그인이 안됐을 때 url에 맞는 ResponsePage을 조회 가능")
    void fromUrlSuccessByIsNotLogin() {
        ResponsePage expected = ResponsePage.LOGIN_IS_NOT_LOGIN;

        Optional<ResponsePage> actual = ResponsePage.fromUrl(expected.getUrl(), new HttpCookie(new HashMap<>()));

        assertThat(actual).isEqualTo(Optional.of(expected));
    }

    @Test
    @DisplayName("성공 : url이 와 연관된 값이 없으면 optional 반환")
    void fromUrlSuccessByNotContaionUrl() {
        Optional<ResponsePage> actual = ResponsePage.fromUrl("/a", new HttpCookie(new HashMap<>()));

        assertThat(actual).isEmpty();
    }
}
