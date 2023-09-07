package nextstep.jwp.controller;

import static org.apache.coyote.http11.common.ContentType.HTML;

import java.io.IOException;
import java.util.Objects;
import javassist.NotFoundException;
import org.apache.catalina.servlet.util.ResourceReader;
import org.apache.coyote.http11.SessionManager.Session;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class ViewController implements HandlerAdaptor {

    private ViewController() {
    }

    public static Response login(final Request request) {
        return responseByLogin(request.getSession(), "/login.html");
    }

    public static Response register(final Request request) {
        return responseByLogin(request.getSession(), "/register.html");
    }

    private static Response responseByLogin(final Session session, final String redirectUri) {
        if (isLoginUser(session)) {
            return Response.redirect("index.html")
                    .build();
        }

        return createResourceResponse(redirectUri);
    }

    private static boolean isLoginUser(final Session session) {
        return Objects.nonNull(session.getAttribute("user"));
    }

    private static Response createResourceResponse(final String fileName) {
        try {
            /// TODO: 2023/09/07 여기서 ResourceReader를 아는 게 맞을까?
            final var resource = ResourceReader.read(fileName);
            final var responseBody = resource.getContentBytes();

            return Response.ok()
                    .body(responseBody)
                    .addContentType(resource.getContentType())
                    .build();
        } catch (final IOException e) {
            return Response.internalSeverError().build();
        } catch (final NotFoundException e) {
            return Response.notFound().build();
        }
    }

    public static Response resource(final Request request) {
        if ("/".equals(request.getUri())) {
            return Response.ok()
                    .body("Hello world!")
                    .addContentType(HTML.toString())
                    .build();
        }
        return createResourceResponse(request.getPath());
    }

}
