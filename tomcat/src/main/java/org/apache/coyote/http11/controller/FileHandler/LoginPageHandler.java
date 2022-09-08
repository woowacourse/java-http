package org.apache.coyote.http11.controller.FileHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Session;

public class LoginPageHandler extends AbstractController {
    private static final String USER = "user";

    private static final Pattern LOGIN_PAGE_URI_PATTERN = Pattern.compile("/login.html");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.GET, LOGIN_PAGE_URI_PATTERN);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Session session = httpRequest.getSession();
        if (session.getAttribute(USER) != null) {
            httpResponse.found("/index.html ")
                    .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                    .addHeader("Content-Length", "0 ");
            return;
        }
        String responseBody = getBody(httpRequest);

        httpResponse.ok(responseBody)
                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ");
    }

    private String getBody(HttpRequest httpRequest) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        File file = new File(resource.getFile());
        Path path = file.toPath();

        return new String(Files.readAllBytes(path));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
