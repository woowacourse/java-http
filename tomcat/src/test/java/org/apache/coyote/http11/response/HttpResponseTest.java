package org.apache.coyote.http11.response;

import common.http.ContentType;
import common.http.Cookie;
import common.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void HttpResponse를_String으로_변환한다() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.addVersionOfTheProtocol("HTTP/1.1");
        httpResponse.addHttpStatus(HttpStatus.OK);
        httpResponse.addCookie(Cookie.from("JSESSIONID=1234"));
        httpResponse.addContentType(ContentType.HTML);
        httpResponse.addBody("로이스 잘생겼다");

        // when
        String string = httpResponse.getMessage();

        // then
        Assertions.assertThat(string).contains("HTTP/1.1 200 OK", "Set-Cookie: JSESSIONID=1234", "Content-Type: text/html", "로이스 잘생겼다");
    }

}
