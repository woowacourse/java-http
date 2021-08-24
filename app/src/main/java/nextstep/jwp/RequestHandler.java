package nextstep.jwp;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.util.List;
import nextstep.jwp.http.Protocol;
import nextstep.jwp.http.controller.StandardController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.Response;
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

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            String inputData = asString(inputStream);
            HttpRequest httpRequest = new HttpRequest(inputData);

            StandardController standardController = standardControllers.stream()
                .filter(controller -> controller.isSatisfiedBy(httpRequest))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("컨트롤러를 찾을 수 없습니다."));

            Response httpResponse = standardController.doService(httpRequest);
            final String response = httpResponse.asString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String asString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder sb = new StringBuilder();

        String line;
        while((line = br.readLine()) != null) {
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
