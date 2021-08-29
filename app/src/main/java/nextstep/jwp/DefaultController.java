package nextstep.jwp;

import java.io.IOException;

public class DefaultController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String responseBody = "Hello world!";
        response.setStatus(200);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
        response.write(responseBody);
        response.flush();
    }
}
