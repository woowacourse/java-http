package nextstep.jwp.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.MethodType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHandler.class);
    protected static final String ACCOUNT = "account";
    protected static final String EMAIL = "email";
    protected static final String PASSWORD = "password";

    protected final Request request;

    protected AbstractHandler(Request request) {
        this.request = request;
    }

    @Override
    public Response message() throws IOException {
        if (MethodType.isGet(request.getRequestMethod())) {
            return getMessage();
        }
        return postMessage();
    }

    @Override
    public Response postMessage() {
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
            LOGGER.error("File not found error", exception);
            throw new IOException();
        }
    }
}
