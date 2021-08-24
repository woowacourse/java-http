package nextstep.jwp;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.controller.StandardController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;
import nextstep.jwp.http.response.response_line.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final List<StandardController> standardControllers;
    private final Socket connection;

    public RequestHandler(Socket connection, List<StandardController> standardControllers) {
        this.connection = Objects.requireNonNull(connection);
        this.standardControllers = standardControllers;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        final InputStream inputStream = getinputStream();
        final OutputStream outputStream = getOutputStream();

        try (inputStream; outputStream) {
            String inputData = asString(inputStream);
            if(inputData.isBlank()) {
                System.out.println("cnd");
                return;
            }

            Response httpResponse = doService(inputData, outputStream);


            writeResponse(outputStream, httpResponse);
        } catch (Exception exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Response doService(String inputData, OutputStream outputStream) throws IOException {
        try {
            HttpRequest httpRequest = new HttpRequest(inputData);
            StandardController standardController = findStandardController(httpRequest);
            return standardController.doService(httpRequest);
        } catch (Exception e) {
            final Headers headers = new Headers();
            final Body body = new Body("test", ContentType.TEXT_PLAIN.asString());
            headers.setBodyHeader(body);

            final HttpResponse httpResponse = new HttpResponse(
                new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.INTERNAL_SERVER_ERROR),
                headers,
                body
            );
            outputStream.write(httpResponse.asString().getBytes());

            throw new IllegalArgumentException();
        }
    }

    private void writeResponse(OutputStream outputStream, Response httpResponse)
        throws IOException {
        final String response = httpResponse.asString();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private StandardController findStandardController(HttpRequest httpRequest) {
        return standardControllers.stream()
            .filter(controller -> controller.isSatisfiedBy(httpRequest))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("컨트롤러를 찾을 수 없습니다."));
    }

    private OutputStream getOutputStream() {
        try {
            return connection.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getinputStream() {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String asString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder sb = new StringBuilder();

        while(!br.ready());

        String line;
        while(br.ready() && (line = br.readLine()) != null) {
            sb.append(line + LINE_SEPARATOR);
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
