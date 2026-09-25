package com.techcourse.controller;

import com.techcourse.StaticResourceReader;
import com.techcourse.model.Register;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

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
        final var responseBody = StaticResourceReader.read(request.getResourcePath());
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

    private static String readStaticResource(MyHttpRequest httpRequest, String defaultContent)
            throws IOException, URISyntaxException {
        URL fileUrl = Http11Processor.class
                .getClassLoader()
                .getResource(httpRequest.getResourcePath());
        File file = new File(Objects.requireNonNull(fileUrl).toURI());
        if (file.isFile()) {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
        return defaultContent;
    }
}
