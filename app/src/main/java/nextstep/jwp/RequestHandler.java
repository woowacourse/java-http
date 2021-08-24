package nextstep.jwp;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.CustomControllerFactory;
import nextstep.jwp.http.controller.standard.StandardController;
import nextstep.jwp.http.controller.standard.StandardControllerFactory;
import nextstep.jwp.http.exception.Exceptions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        final InputStream inputStream = getInputStream();
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

    private InputStream getInputStream() {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private OutputStream getOutputStream() {
        try {
            return connection.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResponse(OutputStream outputStream, Response httpResponse)
        throws IOException {
        final String response = httpResponse.asString();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Response doService(String inputData, OutputStream outputStream) throws IOException {
        try {
            HttpRequest httpRequest = new HttpRequest(inputData);
            Controller controller = findController(httpRequest);

            return controller.doService(httpRequest);
        } catch (Exception e) {
            HttpResponse httpResponse = Exceptions.findResponseByException(e);
            outputStream.write(httpResponse.asString().getBytes());

            throw new IllegalArgumentException();
        }
    }

    private Controller findController(HttpRequest httpRequest) {
        return customControllers.stream()
            .filter(controller -> controller.isSatisfiedBy(httpRequest))
            .findAny()
            .orElse(findStandardController(httpRequest));
    }

    private StandardController findStandardController(HttpRequest httpRequest) {
        return standardControllers.stream()
            .filter(controller -> controller.isSatisfiedBy(httpRequest))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("컨트롤러를 찾을 수 없습니다."));
    }

    private String asString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder sb = new StringBuilder();

        while(!br.ready());

        String line;
        while(br.ready() && (line = br.readLine()) != null) {
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
