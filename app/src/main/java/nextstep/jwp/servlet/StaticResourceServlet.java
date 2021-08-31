package nextstep.jwp.servlet;

import java.io.IOException;
import nextstep.jwp.http.reponse.HttpResponse;
import nextstep.jwp.http.reponse.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.tomcat.Servlet;

public class StaticResourceServlet extends Servlet {

    public StaticResourceServlet() {
        super("/");
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.OK);
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.equals("/")) {
            httpResponse.addFile("/hello.html");
            return;
        }
        httpResponse.addFile(requestURI);
    }

}
