package org.apache.coyote.controller;

import com.techcourse.service.UserService;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

public class RegisterController extends AbstractController {

    private static final String STATIC_ROOT = "static";

    @Override
    protected void doGet(Http11Request request, Http11Response response) {

        HttpStatus statusCode;
        String path = request.getPath();
        FileManager fileManager;

        try {
            fileManager = new FileManager(STATIC_ROOT + "/register.html", path);
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            String body = "500 Internal Server Error";
            response.status(statusCode);
            response.contentType("text/html; charset=utf-8");
            response.body(body.getBytes());

            return;
        }

        String contentType = ContentTypeSearcher.getContentTypeBy("/register.html");
        statusCode = HttpStatus.OK;
        response.status(statusCode);
        response.contentType(contentType);
        response.body(fileManager.getContent());
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        String body = request.getBody();
        Map<String, String> map = Arrays.stream(body.split("&", 2))
                .map(set -> set.split("=", 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr[1]
                ));

        String account = map.get("account");
        String email = map.get("email");
        String password = map.get("password");

        UserService.createUser(account, password, email);

        response.status(HttpStatus.FOUND);
        response.location("/index.html");
    }
}
