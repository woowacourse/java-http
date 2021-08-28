package nextstep.jwp;

import java.io.IOException;

public class ViewController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        String contentType = "text/html;charset=utf-8";
        if (path.endsWith(".css")) {
            contentType = "text/css;charset=utf-8";
        }
        String responseBody = new String(getClass().getClassLoader().getResourceAsStream("static" + path).readAllBytes());
        response.setStatus(200);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
        response.write(responseBody);
        response.flush();
    }
}
