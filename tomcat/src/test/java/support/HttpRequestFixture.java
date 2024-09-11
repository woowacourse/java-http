package support;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpRequestFixture {

    private HttpRequestFixture() {
    }

    public static HttpRequest loginPost() throws IOException {
        final String requestString = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password ");

        return HttpRequestGenerator.generate(requestString);
    }

    public static HttpRequest loginGet() throws IOException {
        final String requestString = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        return HttpRequestGenerator.generate(requestString);
    }

    public static HttpRequest loginGetWithSessionId() throws IOException {
        final String requestString = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=cookie-value ",
                "",
                "");

        return HttpRequestGenerator.generate(requestString);
    }

    public static HttpRequest invalidIdLoginPost() throws IOException {
        final String requestString = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 31",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu2&password=password ");

        return HttpRequestGenerator.generate(requestString);
    }

    public static HttpRequest invalidPasswordLoginPost() throws IOException {
        final String requestString = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 31",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password2 ");

        return HttpRequestGenerator.generate(requestString);
    }

    public static HttpRequest registerPost() throws IOException {
        final String formData = "account=gugu2&password=password&email=hkkang%40woowahan.com";
        final String requestString = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + formData.length(),
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                formData);

        return HttpRequestGenerator.generate(requestString);
    }

    public static HttpRequest registerGet() throws IOException {
        final String requestString = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        return HttpRequestGenerator.generate(requestString);
    }

    public static HttpRequest duplicatedIdRegisterPost() throws IOException {
        final String formData = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String requestString = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + formData.length(),
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                formData);

        return HttpRequestGenerator.generate(requestString);
    }
}
