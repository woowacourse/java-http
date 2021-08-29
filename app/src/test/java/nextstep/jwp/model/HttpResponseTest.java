package nextstep.jwp.model;

import nextstep.jwp.model.httpMessage.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class HttpResponseTest {

    @Test
    void responseForward() throws IOException {
        File file = new File("./src/test/resources/http_forward.txt");
        HttpResponse response = new HttpResponse(new FileOutputStream(file));
        response.forward("/index.html");
    }

    @Test
    void responseRedirect() throws IOException {
        File file = new File("./src/test/resources/http_redirect.txt");
        HttpResponse response = new HttpResponse(new FileOutputStream(file));
        response.redirect("/401.html");
    }
}