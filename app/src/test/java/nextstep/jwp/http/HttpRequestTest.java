package nextstep.jwp.http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {
    private final String testDirectory = "./src/test/resources/static/";

    @Test
    void GET() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(testDirectory + "GET.txt"));
        HttpRequest httpRequest = new HttpRequest(inputStream);

        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getUri()).isEqualTo("/login");
        assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(httpRequest.getParameter("account")).isEqualTo("gugu");
    }

    @Test
    void POST() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(testDirectory + "POST.txt"));
        HttpRequest httpRequest = new HttpRequest(inputStream);

        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(httpRequest.getUri()).isEqualTo("/login");
        assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(httpRequest.getHeader("Content-Length")).isEqualTo("30");
        assertThat(httpRequest.getParameter("account")).isEqualTo("gugu");
    }

    @Test
    void cookie() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(testDirectory + "Cookie.txt"));
        HttpRequest httpRequest = new HttpRequest(inputStream);

        assertThat(httpRequest.getHeader("Cookie")).isEqualTo("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        assertThat(httpRequest.getCookies().get("yummy_cookie")).isEqualTo("choco");
        assertThat(httpRequest.getCookies().get("tasty_cookie")).isEqualTo("strawberry");
        assertThat(httpRequest.getCookies().get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
