package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.FileNotFoundException;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.exception.RegisterException;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.request.MethodType;
import nextstep.jwp.model.request.Request;
import nextstep.jwp.model.response.Response;
import nextstep.jwp.model.response.StatusType;

public abstract class AbstractController implements Controller {

    public Response doService(Request request) throws IOException {
        try {
            if (MethodType.isGet(request.getRequestMethod())) {
                return doGet(request);
            } else if (MethodType.isPost(request.getRequestMethod())) {
                return doPost(request);
            }
            throw new IllegalStateException();
        } catch (FileNotFoundException | IllegalStateException exception) {
            final String responseBody = createResponseBody(PathType.NOT_FOUND.value() + FileType.HTML.extension());
            return staticFileMessage(request, StatusType.NOT_FOUND, FileType.HTML, responseBody);
        } catch (LoginException | RegisterException exception) {
            return redirectMessage(request, PathType.UNAUTHORIZED.resource());
        }
    }

    protected Response doGet(Request request) throws IOException {
        throw new IllegalStateException();
    }

    protected Response doPost(Request request) {
        throw new IllegalStateException();
    }

    protected Response staticFileMessage(Request request, StatusType status, FileType fileType, String responseBody) {
        return new Response.Builder()
                .statusLine(request.getProtocol(), status)
                .contentType(fileType.contentType())
                .contentLength(responseBody.getBytes().length)
                .setCookie(!request.hasCookie(), request.getSession())
                .body(responseBody)
                .build();
    }

    protected Response redirectMessage(Request request, String location) {
        return new Response.Builder()
                .statusLine(request.getProtocol(), StatusType.FOUND)
                .location(location)
                .body("")
                .build();
    }

    protected String createResponseBody(String requestPath) throws IOException {
        try {
            final URL resource = getClass().getClassLoader().getResource("static" + requestPath);
            final Path path = new File(resource.getPath()).toPath();
            return new String(Files.readAllBytes(path));
        } catch (NullPointerException exception) {
            throw new FileNotFoundException();
        }
    }
}
