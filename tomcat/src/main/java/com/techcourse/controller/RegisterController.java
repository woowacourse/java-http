package com.techcourse.controller;

import com.techcourse.StaticResourceReader;
import com.techcourse.model.Register;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws Exception {
        register(request, response);
    }

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setContentType(request.getContentType());
        final var responseBody = StaticResourceReader.read(request.getResourcePath())
                .orElseThrow(() -> new IllegalArgumentException("정적 리소스를 찾을 수 없습니다."));
        response.writeBody(responseBody);
    }

    private void register(MyHttpRequest httpRequest, MyHttpResponse httpResponse) {
        Map<String, String> params = httpRequest.getFormParameters();

        try {
            User registeredUser = Register.register(params.get("account"), params.get("email"), params.get("password"));
            log.info("registration succeed: {}", registeredUser);
            httpResponse.setStatusCode(StatusCode.FOUND);
            httpResponse.setContentType(ContentType.HTML);
            httpResponse.sendRedirect("index.html");
        } catch (IllegalArgumentException e) {
            log.error("registration failed: ", e);
            httpResponse.setStatusCode(StatusCode.FOUND);
            httpResponse.setContentType(ContentType.HTML);
            httpResponse.sendRedirect("login.html");
        }
    }

}
