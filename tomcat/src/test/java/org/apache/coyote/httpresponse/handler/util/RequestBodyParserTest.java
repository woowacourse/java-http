package org.apache.coyote.httpresponse.handler.util;

import nextstep.jwp.model.User;
import org.apache.coyote.httprequest.RequestBody;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
class RequestBodyParserTest {

    @Test
    void 파싱에_성공한다() {
        // given
        final String account = "bebe";
        final String password = "bebebe";
        final String requestBodyInput = "account=" + account + "&password=" + password + "&email=bebe%40woowahan.com";
        final RequestBody requestBody = new RequestBody(requestBodyInput);

        // when
        final User user = RequestBodyParser.parse(requestBody);

        // then
        assertSoftly(softly -> {
            assertThat(user.getAccount()).isEqualTo(account);
            assertThat(user.checkPassword(password)).isTrue();
        });
    }
}
