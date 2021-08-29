package nextstep.jwp.model;

import nextstep.jwp.model.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    public static final String HTTP_FORWARD_TXT = "./src/test/resources/http_forward.txt";
    public static final String HTTP_REDIRECT_TXT = "./src/test/resources/http_redirect.txt";

    @Test
    void responseForward() throws IOException {
        File file = new File(HTTP_FORWARD_TXT);
        HttpResponse response = new HttpResponse(new FileOutputStream(file));
        response.forward("/index.html");

        BufferedReader reader = new BufferedReader(new FileReader(HTTP_FORWARD_TXT));
        assertThat(reader.readLine()).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(reader.readLine()).isEqualTo("Content-Type: text/html;charset=utf-8 ");
        assertThat(reader.readLine()).isEqualTo("Content-Length: 5670 ");
    }

    @Test
    void responseRedirect() throws IOException {
        File file = new File(HTTP_REDIRECT_TXT);
        HttpResponse response = new HttpResponse(new FileOutputStream(file));
        response.redirect("/401.html");

        BufferedReader reader = new BufferedReader(new FileReader(HTTP_REDIRECT_TXT));
        assertThat(reader.readLine()).isEqualTo("HTTP/1.1 302 Redirect ");
        assertThat(reader.readLine()).isEqualTo("Location: /401.html ");
    }
}