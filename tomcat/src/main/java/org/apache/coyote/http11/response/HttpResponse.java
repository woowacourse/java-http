package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.FileContent;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String NEW_LINE = "\r\n";
    private static final String STATIC = "static";

    private final ResponseLine responseLine;
    private final HttpHeaders headers;
    private final String responseBody;

    private HttpResponse(final ResponseLine responseLine, final HttpHeaders headers, final String responseBody) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse parse(final HttpRequest request) throws IOException {
        Path path;
        String uri = request.getUri();
        if (uri.equals("/")) {
            return new HttpResponse(ResponseLine.create(HttpStatus.OK),
                    HttpHeaders.createSimpleText(),
                    "Hello world!");
        }

        final String[] queryUri = uri.split("\\?");

        if (queryUri.length == 1) {
            final String[] splitUri = uri.split("\\.");
            URL url;
            if (splitUri.length == 1) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + FileContent.findPage(uri) + ".html");

                path = new File(url.getPath()).toPath();

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String responseBody = new String(content);

                if (FileContent.findPage(uri).equals("/404")) {
                    return new HttpResponse(ResponseLine.create(HttpStatus.NOT_FOUND), headers, responseBody);
                }
                return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri);

                path = new File(url.getPath()).toPath();

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String responseBody = new String(content);

                return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
            }
        }
        else {
            uri = queryUri[0];
            final String[] parseQuery = queryUri[1].split("&");
            final String username = parseQuery[0].split("=")[1];
            final String password = parseQuery[1].split("=")[1];

            final String[] splitUri = uri.split("\\.");

            URL url;

            if (InMemoryUserRepository.findByAccountAndPassword(username, password).isEmpty()) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + "/401" + ".html");
                path = new File(url.getPath()).toPath();

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String responseBody = new String(content);

                return new HttpResponse(ResponseLine.create(HttpStatus.UNAUTHORIZED), headers, responseBody);
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + "/index" + ".html");
                path = new File(url.getPath()).toPath();

                final User user = InMemoryUserRepository.findByAccountAndPassword(username, password).get();
                log.info(user.toString());

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                headers.setHeader("Location", "/index.html");
                final String responseBody = new String(content);

                return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, responseBody);
            }
        }
    }

    @Override
    public String toString() {
        return responseLine.toString() +
                NEW_LINE +
                headers.toString() +
                NEW_LINE +
                responseBody;
    }
}
