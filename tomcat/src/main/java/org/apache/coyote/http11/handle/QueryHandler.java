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
        final String[] splitRequestTarget = request.getHttpStartLine().getRequestTarget().getOrigin().split("\\?", 2);
        final Map<String, String> totalQuery = extractQuery(splitRequestTarget);
        final HttpVersion httpVersion = request.getHttpStartLine().getHttpVersion();

        final String httpStatus = executionLogic(totalQuery);

        final String responseBody = makeResponseBody(httpStatus);
        return HttpResponse.of(httpVersion, httpStatus, CONTENT_TYPE, responseBody);
    }

    private String makeResponseBody(final String httpStatus) throws IOException {
        final String resourceName = findResourceName(httpStatus);
        final URL resource = getClass().getClassLoader().getResource("static/" + resourceName);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private static String findResourceName(final String httpStatus) {
        if (httpStatus.equals("301")) {
            return "index.html";
        }
        return httpStatus + ".html";
    }

    private static String executionLogic(final Map<String, String> totalQuery) {
        try {
            final UserController userController = new UserController();
            userController.loginlogin(totalQuery);
            return "301";
        } catch (IllegalArgumentException e) {
            return "401";
        }
    }

    private static Map<String, String> extractQuery(final String[] splitRequestTarget) {
        final String querys = splitRequestTarget[1];
        return Arrays.stream(querys.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }
}
