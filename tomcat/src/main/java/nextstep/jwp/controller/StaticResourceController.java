package nextstep.jwp.controller;

import java.util.UUID;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class StaticResourceController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        final Path path = request.getPath();
        if (request.isGetMethod()) {
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

            final Session session = new Session(UUID.randomUUID().toString());
            final SessionManager sessionManager = new SessionManager();
            sessionManager.add(session);

            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .cookie("JSESSIONID=" + session.getId())
                    .contentType(path.getContentType())
                    .responseBody(responseBody)
                    .build();
        }

        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }
}
