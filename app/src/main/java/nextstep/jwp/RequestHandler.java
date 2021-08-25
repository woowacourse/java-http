package nextstep.jwp;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.CustomControllerFactory;
import nextstep.jwp.http.controller.standard.StandardController;
import nextstep.jwp.http.controller.standard.StandardControllerFactory;
import nextstep.jwp.http.exception.Exceptions;
import nextstep.jwp.http.exception.InternalServerException;
import nextstep.jwp.http.exception.NotFoundException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final List<Controller> customControllers;
    private final List<StandardController> standardControllers;
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.standardControllers = StandardControllerFactory.create();
        this.customControllers = CustomControllerFactory.create();
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (
            var br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            var outputStream = connection.getOutputStream()
        ) {
            RequestLine requestLine = new RequestLine(extractRawRequestLine(br));
            Headers headers = new Headers(extractRawHeaders(br));
            Body body = headers.getHeader("Content-Length")
                .map(length -> extractRawBody(br, length))
                .map(Body::new)
                .orElseGet(Body::empty);

            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            Response httpResponse = doService(httpRequest, outputStream);
            if (Objects.isNull(httpResponse)) {
                return;
            }

            writeResponse(outputStream, httpResponse);
        } catch (Exception exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String extractRawRequestLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private String extractRawHeaders(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();

        String line = null;
        while (!"".equals(line)) {
            line = br.readLine();
            if (line == null) {
                break;
            }
            sb.append(line).append(LINE_SEPARATOR);
        }

        return sb.toString();
    }

    private String extractRawBody(BufferedReader br, String length) {
        try {
            int contentLength = Integer.parseInt(length);

            char[] buffer = new char[contentLength];
            br.read(buffer, 0, contentLength);

            return new String(buffer);
        } catch (IOException e) {
            throw new InternalServerException();
        }
    }

    private Response doService(
        HttpRequest httpRequest, OutputStream outputStream
    ) throws IOException {
        try {
            Controller controller = findController(httpRequest);
            return controller.doService(httpRequest);
        } catch (Exception e) {
            responseExceptionPage(outputStream, e);
            return null;
        }
    }

    private void responseExceptionPage(OutputStream outputStream, Exception e) throws IOException {
        Exceptions exceptions = Exceptions.findByException(e);
        HttpResponse httpResponse =
            HttpResponse.status(exceptions.getHttpStatus(), exceptions.getPath());

        outputStream.write(httpResponse.asString().getBytes());
        outputStream.close();
    }

    private void writeResponse(OutputStream outputStream, Response httpResponse)
        throws IOException {
        final String response = httpResponse.asString();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Controller findController(HttpRequest httpRequest) {
        return customControllers.stream()
            .filter(controller -> controller.isSatisfiedBy(httpRequest))
            .findAny()
            .orElseGet(() -> findStandardController(httpRequest));
    }

    private StandardController findStandardController(HttpRequest httpRequest) {
        return standardControllers.stream()
            .filter(controller -> controller.isSatisfiedBy(httpRequest))
            .findAny()
            .orElseThrow(NotFoundException::new);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
