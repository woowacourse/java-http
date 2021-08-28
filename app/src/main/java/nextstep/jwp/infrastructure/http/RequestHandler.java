package nextstep.jwp.infrastructure.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.infrastructure.http.controller.GetRegisterController;
import nextstep.jwp.infrastructure.http.controller.HelloController;
import nextstep.jwp.infrastructure.http.controller.LoginController;
import nextstep.jwp.infrastructure.http.controller.PostRegisterController;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Socket connection;
    private final ControllerMapping controllerMapping;
    private final ViewResolver viewResolver;

    public RequestHandler(final Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        this.controllerMapping = new ControllerMapping(
            Arrays.asList(
                new HelloController(),
                new LoginController(),
                new GetRegisterController(),
                new PostRegisterController()
            )
        );
        this.viewResolver = new ViewResolver("static");
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest request = requestFromReader(bufferedReader);
            final View view = findViewByRequest(request);
            final HttpResponse httpResponse = viewResolver.resolve(view);

            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (IllegalArgumentException exception) {
            log.error("Exception occurs", exception);
        } finally {
            close();
        }
    }

    private HttpRequest requestFromReader(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestLine requestLine = requestLineFromReader(bufferedReader);
        final HttpHeaders headers = headerFromReader(bufferedReader);
        final String body = bodyFromReader(bufferedReader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private HttpRequestLine requestLineFromReader(final BufferedReader bufferedReader) throws IOException {
        return HttpRequestLine.of(bufferedReader.readLine());
    }

    private HttpHeaders headerFromReader(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;

        while (Objects.nonNull(line = bufferedReader.readLine())) {
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }

        return HttpHeaders.of(lines);
    }

    private String bodyFromReader(final BufferedReader bufferedReader, final HttpHeaders headers) throws IOException {
        if (!headers.hasKey(CONTENT_LENGTH)) {
            return "";
        }

        int contentLength = Integer.parseInt(headers.getValue(CONTENT_LENGTH).get(0));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private View findViewByRequest(final HttpRequest request) {
        if (controllerMapping.contains(request)) {
            return controllerMapping.handle(request);
        }
        return View.buildByResource(request.getRequestLine().getUri().getBaseUri());
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
