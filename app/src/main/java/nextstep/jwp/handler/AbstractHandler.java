package nextstep.jwp.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractHandler implements Handler {

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
        final URL resource = getClass().getClassLoader().getResource("static" + requestPath);
        final Path path = new File(resource.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
