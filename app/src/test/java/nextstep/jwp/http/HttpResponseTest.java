package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.entity.HttpCookie;
import org.junit.jupiter.api.Test;

class HttpResponseTest {
    @Test
    void asString() {
        HttpResponse httpResponse = new HttpResponse();

        String actual = httpResponse.asString();
        assertThat(actual).contains("HTTP/1.1 200 OK");
    }

    @Test
    void redirect() {
        HttpResponse httpResponse = new HttpResponse();
        String path = "/index.html";
        httpResponse.redirect(path);
        String actual = httpResponse.asString();
        System.out.println(actual);

        assertThat(actual).contains("HTTP/1.1 302 Found");
        assertThat(actual).contains("Location: " + path);
    }

    @Test
    void setCookie() {
        HttpResponse httpResponse = new HttpResponse();
        String cookieLine = "JSESSIONID=hello";

        HttpCookie httpCookie = HttpCookie.of(cookieLine);
        httpResponse.setCookie(httpCookie);
        String actual = httpResponse.asString();

        System.out.println(actual);

        assertThat(actual).contains("HTTP/1.1 200 OK");
        assertThat(actual).contains("Set-Cookie: " + cookieLine);
    }
}
