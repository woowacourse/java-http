package org.apache.coyote.http11.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RequestDataTest {

    @Test
    void query_parameter를_파싱한다() {
        String queryData = "/login?account=boxter&password=password";

        RequestData requestData = RequestData.of(queryData, "");

        assertThat(requestData.find("account")).isEqualTo("boxter");
    }

    @Test
    void reqeust_body_를_파싱한다() {
        String queryData = "account=boxter&password=password";
        RequestData requestData = RequestData.of("", queryData);

        assertThat(requestData.find("account")).isEqualTo("boxter");
    }

    @Test
    void query_String과_reqeust_body_를_파싱한다() {
        String email = "email";
        String emailValue = "boxterEmail";

        String uriPassword = "uriPassword";
        String uriPasswordValue = "uriPassword";

        String account = "account";
        String accountValue = "boxter";

        String password = "password";
        String passwordValue = "password";

        String requestUri = "/login?" + email + "=" + emailValue + "&" + uriPassword + "=" + uriPasswordValue;
        String requestBody = account + "=" + accountValue + "&" + password + "=" + passwordValue;

        RequestData requestData = RequestData.of(requestUri, requestBody);
        assertAll(
                () -> assertThat(requestData.find(email)).isEqualTo(emailValue),
                () -> assertThat(requestData.find(uriPassword)).isEqualTo(uriPasswordValue),
                () -> assertThat(requestData.find(account)).isEqualTo(accountValue),
                () -> assertThat(requestData.find(password)).isEqualTo(passwordValue)
        );

    }
}
