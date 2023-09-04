package nextstep.handler;

import common.FileReader;
import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler {

    private static final String TEXT_HTML = "text/html;charset=utf-8";
    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private Handler() {
    }

    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException {
        final var path = httpRequest.getPath();
        HttpMethod method = httpRequest.getHttpMethod();
        if (method == HttpMethod.GET && path.equals("/")) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.setBody("Hello world!");
            return;
        }
        if (method == HttpMethod.GET && path.equals("/index.html")) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
            httpResponse.setBody(FileReader.readFile(path));
            return;
        }
        if (method == HttpMethod.GET && (path.equals("/login") || path.equals("/login.html"))) {
            final var parameters = httpRequest.getParameters();
            if (parameters.containsKey("account") && parameters.containsKey("password")) {
                login(httpResponse, parameters);
                return;
            }
            final var responseBody = FileReader.readFile(httpRequest.getUri());
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
            httpResponse.setBody(responseBody);
            return;
        }
        if (method == HttpMethod.GET && path.endsWith(".html")) {
            final var responseBody = FileReader.readFile(httpRequest.getUri());
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
            httpResponse.setBody(responseBody);
            return;
        }
        if (method == HttpMethod.GET) {
            final var responseBody = FileReader.readFile(httpRequest.getUri());
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE,
                    httpRequest.getHeader(HttpHeaders.ACCEPT));
            httpResponse.setBody(responseBody);
        }
    }

    private static void login(HttpResponse httpResponse, Map<String, String> parameters) {
        InMemoryUserRepository.findByAccount(parameters.get("account"))
                .filter(user -> user.checkPassword(parameters.get("password")))
                .ifPresentOrElse(user -> success(httpResponse), () -> fail(httpResponse));
    }

    private static void success(HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
        httpResponse.addHeader(HttpHeaders.LOCATION, "/index.html");
    }

    private static void fail(HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
        httpResponse.addHeader(HttpHeaders.LOCATION, "/401.html");
    }
}
