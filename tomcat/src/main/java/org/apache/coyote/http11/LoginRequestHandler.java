package org.apache.coyote.http11;

import org.apache.catalina.Session;

import java.net.URL;
import java.nio.file.Path;

public class LoginRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET &&
                httpRequest.getRequestUrl()
                        .startsWith("/login");
    }

    @Override
    public void response(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Session session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            httpResponse.redirect("http://localhost:8080/index.html");
            return;
        }

        URL resource = getClass().getClassLoader()
                .getResource("static/login.html");
        Path resourcePath = Path.of(resource.getPath());

        httpResponse.ok()
                .writeStaticResource(resourcePath);
    }
}
