package nextstep.jwp.handler;

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

public abstract class AbstractHandler implements Handler {

    public Response message(Request request) throws IOException {
        try {
            if (MethodType.isGet(request.getRequestMethod())) {
                return getMessage(request);
            }
            return postMessage(request);
        } catch (FileNotFoundException exception) {
            return new Response(redirectMessage(PathType.NOT_FOUND.resource()));
        } catch (LoginException | RegisterException exception) {
            return new Response(redirectMessage(PathType.UNAUTHORIZED.resource()));
        }
    }

    protected Response getMessage(Request request) throws IOException {
        throw new IllegalStateException();
    }

    protected Response postMessage(Request request) {
        throw new IllegalStateException();
    }

    protected String staticFileMessage(FileType fileType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + fileType.type() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    protected String redirectMessage(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + location + " ",
                "",
                "");
    }

    protected String fileByPath(String requestPath) throws IOException {
        try {
            final URL resource = getClass().getClassLoader().getResource("static" + requestPath);
            final Path path = new File(resource.getPath()).toPath();
            return new String(Files.readAllBytes(path));
        } catch (NullPointerException exception) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }
    }
}
