package nextstep.jwp.server.handler;

import nextstep.jwp.http.exception.Exceptions;
import nextstep.jwp.http.exception.InternalServerException;
import nextstep.jwp.http.message.element.Body;
import nextstep.jwp.http.message.element.Headers;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.request.request_line.RequestLine;
import nextstep.jwp.http.message.response.HttpResponse;
import nextstep.jwp.http.message.response.Response;
import nextstep.jwp.server.handler.controller.Controller;
import nextstep.jwp.server.handler.controller.HandlerMapper;
import nextstep.jwp.server.handler.controller.static_files.StaticFilesControllerFactory;
import nextstep.jwp.web.presentation.controller.factory.CustomControllerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final HandlerMapper handlerMapper;
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.handlerMapper = new HandlerMapper(CustomControllerFactory.create(), StaticFilesControllerFactory.create());
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
            Body body = createBody(br, headers);

            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            Response httpResponse = doService(httpRequest);
            writeResponse(outputStream, httpResponse);
        } catch (Exception exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Body createBody(BufferedReader br, Headers headers) {
        return headers.getHeader("Content-Length")
                .map(length -> extractRawBody(br, length))
                .map(Body::new)
                .orElseGet(Body::empty);
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
            sb.append(line).append(LINE_SEPARATOR.value());
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

    private Response doService(HttpRequest httpRequest) {
        try {
            Controller controller = handlerMapper.findController(httpRequest);
            return controller.doService(httpRequest);

        } catch (Exception e) {
            return responseExceptionPage(e);
        }
    }

    private Response responseExceptionPage(Exception e) {
        Exceptions exceptions = Exceptions.findByException(e);
        return HttpResponse.status(exceptions.getHttpStatus(), exceptions.getPath());
    }

    private void writeResponse(OutputStream outputStream, Response httpResponse)
        throws IOException {
        final String response = httpResponse.asString();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
