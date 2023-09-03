package org.apache.coyote.http11.handle;

import nextstep.jwp.controller.UserController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpVersion;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryHandler extends Handler {
    public static final String CONTENT_TYPE = "text/html;charset=utf-8";


    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        String httpStatus = "200 OK";
        final String[] splitRequestTarget = request.getHttpStartLine().getRequestTarget().getOrigin().split("\\?", 2);
        final Map<String, String> totalQuery = extractQuery(splitRequestTarget);
        final HttpVersion httpVersion = request.getHttpStartLine().getHttpVersion();
        final String responseBody = makeResponseBody(request.getHttpStartLine().getRequestTarget().getOrigin());

        httpStatus = executionLogic(httpStatus, totalQuery);

        return HttpResponse.of(httpVersion, httpStatus, CONTENT_TYPE, responseBody);
    }

    private String makeResponseBody(final String requestTarget) throws IOException {
        final String resourceName = requestTarget.substring(0, requestTarget.indexOf("?")) + ".html";
        final URL resource = getClass().getClassLoader().getResource("static/" + resourceName);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private static String executionLogic(String httpStatus, final Map<String, String> totalQuery) {
        try {
            final UserController userController = new UserController();
            userController.loginlogin(totalQuery);
        } catch (IllegalArgumentException e) {
            httpStatus = "400 UNAUTHORIZED";
        }
        return httpStatus;
    }

    private static Map<String, String> extractQuery(final String[] splitRequestTarget) {
        final String querys = splitRequestTarget[1];
        return Arrays.stream(querys.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }
}
