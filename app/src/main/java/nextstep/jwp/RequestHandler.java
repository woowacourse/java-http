package nextstep.jwp;

import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.RequestConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String NEW_LINE = "\r\n";

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final HttpRequest httpRequest = RequestConverter.convertToHttpRequest(reader);
            String responseBody = getResponseBody(httpRequest.getUri());
            final String response = createResponse(responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String getResponseBody(String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        }
        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource == null) {
            throw new IOException("fileName으로 찾은 resource 값이 null 입니다.");
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String createResponse(String responseBody) {
        final String response = String.join(NEW_LINE,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return response;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
