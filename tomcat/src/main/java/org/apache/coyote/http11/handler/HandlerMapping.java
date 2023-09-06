package org.apache.coyote.http11.handler;

import nextstep.jwp.controller.UserController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class HandlerMapping {

    public String extractResponseEntity(HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        HttpMethod httpMethod = requestLine.getHttpMethod();
        String path = requestLine.getPath();

        ResponseEntity responseEntity = getResponseEntity(request, httpMethod, path);

        return extractResponse(responseEntity);
    }

    private ResponseEntity getResponseEntity(HttpRequest request, HttpMethod httpMethod, String path) {
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

        return new ResponseEntity(HttpMethod.GET, HttpStatus.OK, path);
    }

    private String extractResponse(ResponseEntity responseEntity) {
        if (responseEntity.hasSameHttpMethod(HttpMethod.GET)) {
            ViewResolver viewResolver = new ViewResolver(responseEntity);
            HttpResponse httpResponse = viewResolver.extractHttpResponse();

            return httpResponse.extractResponse();
        }
        return redirect(responseEntity);
    }

    private String redirect(ResponseEntity responseEntity) {
        HttpStatus httpStatus = responseEntity.getHttpStatus();

        return new StringBuilder()
                .append(String.format("HTTP/1.1 %s %s ", httpStatus.getStatusCode(), httpStatus.name())).append("\r\n")
                .append(String.format("Content-Type: %s;charset=utf-8 ", MimeType.HTML)).append("\r\n")
                .append(String.format("Location: %s.html", responseEntity.getPath())).append("\r\n")
                .append("\r\n")
                .toString();
    }

}
