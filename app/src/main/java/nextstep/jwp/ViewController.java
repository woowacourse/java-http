package nextstep.jwp;

import java.io.IOException;

public class ViewController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        try {
            response.forward(request.getPath());
        } catch (IOException e) {
            response.sendRedirect("/404.html");
        }
    }
}
