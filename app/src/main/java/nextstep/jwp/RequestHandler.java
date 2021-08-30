package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.HttpHeaders;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));

            HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            HttpResponse httpResponse = service(httpRequest);

            /*List<String> request = new ArrayList<>();
            while (bufferedReader.ready()) {
                request.add(bufferedReader.readLine());
            }

            request.forEach(System.out::println);

            String requestURI = "static" + request.get(0).split(" ")[1];
            System.out.println(requestURI + "##################");
            URL resource = Objects
                .requireNonNull(getClass().getClassLoader().getResource(requestURI));

            final Path path = new File(resource.getFile()).toPath();
            final String responseBody = Files.readString(path);

            final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);*/

            writeResponse(outputStream, httpResponse);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        final String responseByte = response.asString();
        outputStream.write(responseByte.getBytes());
        outputStream.flush();
    }

    private HttpResponse service(HttpRequest httpRequest) {
        // find controller and process
        return null;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
