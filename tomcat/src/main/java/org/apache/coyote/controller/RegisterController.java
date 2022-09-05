package org.apache.coyote.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.UserService;
import org.apache.coyote.http11.httpmessage.common.ContentType;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.request.requestbody.RequestBodyContent;
import org.apache.coyote.http11.httpmessage.response.Response;

public class RegisterController extends AbstractController {

    @Override
    public void service(Request request, Response response) throws Exception {
        if (request.isPostMethod()) {
            doPost(request, response);
            return;
        }
        if (request.isGetMethod()) {
            doGet(request, response);
        }
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        final RequestBodyContent userInput = RequestBodyContent.parse(request.getBody());
        UserService.save(userInput);

        response.redirect("/index.html")
                .addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final Path path = new File(resource.getPath()).toPath();

        final String responseBody = new String(Files.readAllBytes(path));

        response.ok(responseBody)
                .addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
    }
}
