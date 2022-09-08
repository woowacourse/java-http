package org.apache.coyote.http11.controller.ApiHandler;

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

public class RegisterPageApiController extends AbstractController {

    private static final Pattern REGISTER_URI_PATTERN = Pattern.compile("/register");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.GET, REGISTER_URI_PATTERN);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String responseBody = getBody();

        httpResponse.ok(responseBody)
                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ");
    }

    private String getBody() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/register.html");
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
