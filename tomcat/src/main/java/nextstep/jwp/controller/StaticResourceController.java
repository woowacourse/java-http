package nextstep.jwp.controller;

import org.apache.catalina.Session;
import org.apache.catalina.SessionFactory;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.startline.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class StaticResourceController implements Controller {

    private static final String SESSION_KEY = "JSESSIONID=";

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isGetMethod()) {
            return doGet(request);
        }

        throw new MethodNotAllowedException();
    }

    private HttpResponse doGet(HttpRequest request) {
        final Path path = request.getPath();

        if (path.isIcoContentType()) {
            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .contentType(path.getContentType())
                    .responseBody("")
                    .build();
        }
        final String responseBody = ResourceFindUtils.getResourceFile(path.getResource());

        if (request.containsSession()) {
            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .contentType(path.getContentType())
                    .responseBody(responseBody)
                    .build();
        }

        return toHttpResponseWithCreatingSession(path, responseBody);
    }

    private HttpResponse toHttpResponseWithCreatingSession(Path path, String responseBody) {
        final Session session = SessionFactory.create();
        return new HttpResponse.Builder()
                .status(HttpStatus.OK)
                .cookie(SESSION_KEY + session.getId())
                .contentType(path.getContentType())
                .responseBody(responseBody)
                .build();
    }
}
