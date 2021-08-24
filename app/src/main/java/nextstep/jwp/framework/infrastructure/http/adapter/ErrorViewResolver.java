package nextstep.jwp.framework.infrastructure.http.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.jwp.framework.infrastructure.http.HttpHandler;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorViewResolver implements RequestAdapter {

    private static final Logger log = LoggerFactory.getLogger(HttpHandler.class);
    private static final String STATIC_FILE_FORMAT = "static/%s.html";
    private static final String RESPONSE_FORMAT =
        String.join("\r\n",
            "HTTP/1.1 %s %s ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: %d ", "", "%s"
        );

    private final HttpStatus httpStatus;

    public ErrorViewResolver(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String execute(HttpRequest httpRequest) {
        String errorCode = httpStatus.getErrorCode();
        String errorMessage = httpStatus.getErrorMessage();
        String url = String.format(STATIC_FILE_FORMAT, errorCode);
        URL resource = Thread.currentThread().getContextClassLoader()
            .getResource(url);
        try {
            Path path = Paths.get(resource.toURI());
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            return String.format(
                RESPONSE_FORMAT,
                errorCode,
                errorMessage,
                responseBody.getBytes().length,
                responseBody
            );
        } catch (IOException | URISyntaxException exception) {
            log.error("Exception IO or Invalid File Path", exception);
            throw new IllegalStateException(exception);
        }
    }
}
