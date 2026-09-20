package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.pageController.PageController;
import org.apache.coyote.http11.pageController.PageControllerMapper;
import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Http11Processor implements Runnable, Processor {
    private static final Set<HttpMethod> SUPPORTED_METHODS = Set.of(HttpMethod.GET, HttpMethod.POST);

    private static final String SERVER_ERROR_PAGE = "/500.html";
    private static final StaticResourceLoader STATIC_RESOURCE_LOADER = new StaticResourceLoader();

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {

            try {
                HttpRequest request = getRequestTarget(inputStream);
                PageController controller = PageControllerMapper.getPageController(request.getHttpPath());

                HttpResponse response = handle(request, controller);
                issueSessionCookie(request, response);

                writeResponse(outputStream, response);
            } catch (BadRequestException e) {
                writeResponse(outputStream, badRequest(e));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    
    private void issueSessionCookie(HttpRequest request, HttpResponse response) {
        if (request.getSession(false) != null) {
            return;
        }

        Session session = request.getSession(true);
        response.addCookie(HttpCookie.JSESSIONID, session.getId());
    }

    HttpResponse handle(HttpRequest request, PageController controller) {
        try {
            return controller.run(request);
        } catch (BadRequestException e) {
            return badRequest(e);
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage(), e);
            return internalServerError();
        }
    }

    private HttpResponse badRequest(BadRequestException e) {
        log.warn(e.getMessage());
        return HttpResponse.of(HttpStatus.BAD_REQUEST, "text/plain", e.getMessage());
    }

    private HttpResponse internalServerError() {
        try {
            return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, STATIC_RESOURCE_LOADER.load(SERVER_ERROR_PAGE));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "text/plain", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }

    private HttpRequest getRequestTarget(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 존재하지 않습니다.");
        }

        HttpHeaders headers = readHeaders(reader);
        HttpBody body = readBody(reader, headers.getContentLength());

        return HttpRequest.from(requestLine, headers, body, SUPPORTED_METHODS);
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }

        if (line == null) {
            throw new IOException("HTTP 헤더가 빈 줄로 끝나지 않았습니다.");
        }

        return HttpHeaders.from(headerLines);
    }

    private HttpBody readBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return HttpBody.empty();
        }

        char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("HTTP body가 Content-Length보다 짧습니다.");
            }
            totalRead += read;
        }

        return new HttpBody(new String(buffer));
    }
}
