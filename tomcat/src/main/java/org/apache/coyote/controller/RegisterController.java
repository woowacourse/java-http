package org.apache.coyote.controller;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.util.ResourceReader;
import org.apache.util.parser.BodyParserFactory;
import org.apache.util.parser.Parser;

public class RegisterController extends FrontController {

    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Parser parser = BodyParserFactory.getParser(request.getContentType());
        Map<String, String> params = parser.parse(request.getBody());

        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        userService.registerUser(account, password, email);

        String path = "static/index.html";
        String resource = ResourceReader.readResource(path);

        response.setStatusLine(HttpStatus.OK);
        response.setContentType(ContentType.TEXT_HTML);
        response.setBody(resource);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = "static/register.html";
        String resource = ResourceReader.readResource(path);

        response.setStatusLine(HttpStatus.OK);
        response.setContentType(ContentType.TEXT_HTML);
        response.setBody(resource);
    }
}
