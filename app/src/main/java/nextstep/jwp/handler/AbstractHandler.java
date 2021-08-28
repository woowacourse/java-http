package nextstep.jwp.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHandler.class);
    protected static final String GET = "GET";
    protected static final String HTML = "html";
    protected static final String CSS = "css";
    protected static final String JS = "js";
    protected static final String ACCOUNT = "account";
    protected static final String EMAIL = "email";
    protected static final String PASSWORD = "password";
    protected static final String FILE_401_HTML = "/401.html";
    protected static final String FILE_INDEX_HTML = "/index.html";
    protected static final String HTML_EXTENSION = ".html";

    @Override
    public String message(Request request) throws IOException {
        if (GET.equals(request.getRequestMethod())) {
            return getMessage(request);
        }
        return postMessage(request);
    }

    @Override
    public String postMessage(Request request) {
        throw new IllegalStateException();
    }

    protected String staticFileMessage(String fileType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + fileType + ";charset=utf-8 ",
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
