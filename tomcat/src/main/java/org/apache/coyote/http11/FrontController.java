package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.coyote.http11.SessionManager.Session;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Pattern RESOURCE_PATTERN_FILE_EXTENSION = Pattern.compile(".*\\.[^.]+");
    private static final Logger log = LoggerFactory.getLogger(FrontController.class);
    private static final HandlerAdaptor handlerAdaptor = new HandlerAdaptor();

    private FrontController() {
    }

    public static Response handle(final Request request) throws IOException {
        if (request.getMethod() == GET) {
            return get(request);
        }
        if (request.getMethod() == POST) {
            return post(request);
        }
        return Response.badRequest()
                .addContentType("text/plain")
                .build();
    }

    private static Response get(final Request request) throws IOException {
        final var uri = request.getUri();
        final var session = request.getSession();

        if ("/".equals(uri)) {
            return Response.ok()
                    .addContentType(HTML.toString())
                    .body("Hello world!")
                    .build();
        }
        if ("/login".equals(uri)) {
            return responseByLogin(session, "/login.html");
        }
        if ("/register".equals(uri)) {
            return responseByLogin(session, "/register.html");
        }

        if (isStaticResourceURI(uri)) {
            return getResponseForStaticResource(uri);
        }

        return handlerAdaptor.getMapping(request);
    }

    private static Response responseByLogin(final Session session, final String redirectUri) throws IOException {
        if (isLoginUser(session)) {
            return Response.redirect("index.html")
                    .build();
        }
        return getResponseForStaticResource(redirectUri);
    }

    private static boolean isLoginUser(final Session session) {
        return Objects.nonNull(session.getAttribute("user"));
    }

    private static boolean isStaticResourceURI(final String uri) {
        final var path = Paths.get(uri);
        final var fileName = path.getFileName().toString();
        return RESOURCE_PATTERN_FILE_EXTENSION
                .matcher(fileName)
                .matches();
    }

    private static Response getResponseForStaticResource(final String uri) throws IOException {
        final var url = FrontController.class.getClassLoader().getResource("static" + uri);

        return createStaticResourceResponse(url);
    }

    private static Response createStaticResourceResponse(final URL url) throws IOException {
        if (Objects.isNull(url)) {
            log.warn("static resource url is null");
            return Response.notFound()
                    .addContentType("text/plain")
                    .build();
        }
        log.info("static resource url found: {}", url.getPath());

        final var path = Paths.get(url.getPath());
        final var responseBody = new String(Files.readAllBytes(path));
        return Response.ok()
                .addContentType(Files.probeContentType(path))
                .body(responseBody)
                .build();
    }

    private static Response post(final Request request) {

        return handlerAdaptor.postMapping(request);
    }

}
