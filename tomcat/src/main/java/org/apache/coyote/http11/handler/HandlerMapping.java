package org.apache.coyote.http11.handler;

import java.io.File;
import nextstep.jwp.controller.UserController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class HandlerMapping {

    public HttpResponse extractHttpResponse(HttpRequest request) {
        ResponseEntity responseEntity = getResponseEntity(request);
        File viewFile = ViewResolver.findViewFile(responseEntity.getPath());

        return new HttpResponse(responseEntity.getHttpStatus(), viewFile);
    }

    private ResponseEntity getResponseEntity(HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        HttpMethod httpMethod = requestLine.getHttpMethod();
        String path = requestLine.getPath();

        if (httpMethod.isEqualTo(HttpMethod.POST)) {
            UserController userController = new UserController(new UserService());
            RequestBody requestBody = request.getRequestBody();
            if ("/login".equals(path)) {
                return userController.login(requestBody);
            }
            if ("/register".equals(path)) {
                return userController.signUp(requestBody);
            }
        }

        return new ResponseEntity(HttpStatus.OK, path);
    }

}
