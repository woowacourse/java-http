package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

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
        final String[] queryUri = uri.split("\\?");

        if (queryUri.length == 1) {
            final String[] splitUri = uri.split("\\.");
            URL url;
            if (splitUri.length == 1) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri + ".html");
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri);
            }

            path = new File(url.getPath()).toPath();
        }
        else {
            uri = queryUri[0];
            final String[] parseQuery = queryUri[1].split("&");
            final String username = parseQuery[0].split("=")[1];
            final String password = parseQuery[1].split("=")[1];

            final String[] splitUri = uri.split("\\.");

            URL url;
            if (splitUri.length == 1) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri + ".html");
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri);
            }

            path = new File(url.getPath()).toPath();

            if (!InMemoryUserRepository.findByAccount(username).isPresent()) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + "/401" + ".html");
                path = new File(url.getPath()).toPath();

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String responseBody = new String(content);

                return new HttpResponse(ResponseLine.create(HttpStatus.UNAUTHORIZED), headers, responseBody);
            } else {

            }
        }

        final byte[] content = Files.readAllBytes(path);

        final HttpHeaders headers = HttpHeaders.createResponse(path);
        final String responseBody = new String(content);

        return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
    }

    private static Path findPath(final HttpRequest request) {
        String uri = request.getUri();
        final String[] queryUri = uri.split("\\?");

        if (queryUri.length == 1) {
            final String[] splitUri = uri.split("\\.");
            URL url;
            if (splitUri.length == 1) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri + ".html");
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri);
            }

            return new File(url.getPath()).toPath();
        }
        else {
            uri = queryUri[0];
            final String[] parseQuery = queryUri[1].split("&");
            final String username = parseQuery[0];
            final String password = parseQuery[1];
            System.out.println("@@@ " + InMemoryUserRepository.findByAccount(username).isPresent());

            final String[] splitUri = uri.split("\\.");

            URL url;
            if (splitUri.length == 1) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri + ".html");
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri);
            }

            return new File(url.getPath()).toPath();
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
