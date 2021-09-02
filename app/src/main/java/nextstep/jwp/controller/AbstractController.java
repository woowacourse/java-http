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
import nextstep.jwp.model.MethodType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public abstract class AbstractController implements Controller {

    public Response doService(Request request) throws IOException {
        try {
            if (MethodType.isGet(request.getRequestMethod())) {
                return doGet(request);
            }
            return doPost(request);
        } catch (FileNotFoundException exception) {
            return redirectMessage(request, PathType.NOT_FOUND.resource());
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

    protected Response staticFileMessage(Request request, FileType fileType, String responseBody) {
        return new Response.Builder(request)
                .statusCode("200")
                .statusText("OK")
                .contentType(fileType.contentType())
                .contentLength(responseBody.getBytes().length)
                .body(responseBody)
                .build();
    }

    protected Response redirectMessage(Request request, String location) {
        return new Response.Builder(request)
                .redirect(true)
                .statusCode("302")
                .statusText("FOUND")
                .location(location)
                .build();
    }

    protected String createResponseBody(String requestPath) throws IOException {
        try {
            final URL resource = getClass().getClassLoader().getResource("static" + requestPath);
            final Path path = new File(resource.getPath()).toPath();
            return new String(Files.readAllBytes(path));
        } catch (NullPointerException exception) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }
    }
}
