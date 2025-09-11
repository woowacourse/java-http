package org.apache.catalina.requesthandler;

import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);
    private static final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getPath().equals("/index") || request.getPath().equals("/index.html")) {
            if (sessionManager.getSession(request.getSessionId()) == null) {
                response.setResponseStatus(ResponseStatus.FOUND);
                response.setLocation("/login.html");
                return;
            }
        }

        if (request.getPath().equals("/login") || request.getPath().equals("/login.html") ||
                request.getPath().equals("/register") || request.getPath().equals("/register.html")) {
            if (sessionManager.getSession(request.getSessionId()) != null) {
                final var session = sessionManager.getSession(request.getSessionId());
                User user = (User) session.getAttribute("user");
                response.setResponseStatus(ResponseStatus.FOUND);
                response.setLocation("/index.html");
                return;
            }
        }

        final String staticFilePath = getStaticFilePath(request);
        final byte[] body = readFile(staticFilePath);
        final var contentType = ContentType.fromFileName(staticFilePath);

        response.setResponseStatus(ResponseStatus.OK);
        response.setContentType(contentType);
        response.setBody(body);
    }

    private String getStaticFilePath(HttpRequest httpRequest) {
        final var staticFilePath = "static" + httpRequest.getPath();
        if (httpRequest.getContentType() == ContentType.HTML && !staticFilePath.endsWith(".html")) {
            return staticFilePath + "." + ContentType.HTML.getExtension();
        }
        return staticFilePath;
    }

    private byte[] readFile(String staticFilePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(staticFilePath)) {
            if (is == null) {
                throw new IllegalArgumentException("존재하지 않는 리소스입니다.: " + staticFilePath);
            }
            return is.readAllBytes();
        } catch (IOException e) {
            log.error("파일을 불러오는데 실패했습니다. : {} {}", staticFilePath, e.getMessage(), e);
            throw new IllegalArgumentException("파일을 불러오는데 실패했습니다.: " + staticFilePath, e);
        }
    }
}