package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.infrastructure.http.HttpHeaders;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest httpRequest = HttpRequest.of(splitFromInputStream(bufferedReader));

            final String responseBody = "Hello world!";

            final HttpResponse httpResponse = new HttpResponse(
                new HttpStatusLine(httpRequest.getRequestLine().getHttpVersion(), HttpStatusCode.OK),
                new HttpHeaders.Builder()
                    .header("Content-Type", "text/html;charset=utf-8")
                    .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                    .build(),
                responseBody
            );

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

    private List<String> splitFromInputStream(final BufferedReader bufferedReader) throws IOException {
        final List<String> splitResult = new ArrayList<>();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (Objects.isNull(line)) {
                break;
            }

            splitResult.add(line);
        }

        log.debug(String.join(System.lineSeparator(), splitResult));

        return splitResult;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
