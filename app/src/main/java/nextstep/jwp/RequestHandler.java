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
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.CustomControllerFactory;
import nextstep.jwp.http.controller.standard.StandardController;
import nextstep.jwp.http.controller.standard.StandardControllerFactory;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.exception.Exceptions;
import nextstep.jwp.http.exception.InternalServerException;
import nextstep.jwp.http.exception.NotFoundException;
import nextstep.jwp.http.request.HttpRequest;
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

        try (final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final OutputStream outputStream = connection.getOutputStream()
        ) {
            String rawHeaders = extractRawHeaders(br);
            HttpRequest httpRequest = new HttpRequest(rawHeaders);

            httpRequest.getHeader("Content-Length")
                .ifPresent(length -> getAndSetBody(br, httpRequest, length));

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

    private void getAndSetBody(BufferedReader br, HttpRequest httpRequest, String length) {
        try {
            int contentLength = Integer.parseInt(length);

            char[] buffer = new char[contentLength];
            br.read(buffer, 0, contentLength);

            final String contentType = httpRequest.getHeader("Content-Type")
                .orElseThrow(BadRequestException::new);

            httpRequest.setBody(new Body(new String(buffer), contentType));
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
            HttpResponse httpResponse = Exceptions.findResponseByException(e);
            outputStream.write(httpResponse.asString().getBytes());
            outputStream.close();
            return null;
        }
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

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
