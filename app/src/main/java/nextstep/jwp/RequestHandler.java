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
import java.util.Objects;
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

            String[] parsedRequest = getParsedRequest(inputStream);
            String responseBody = responseBody = getStaticFileContents(parsedRequest);

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String getStaticFileContents(String[] parsedRequest) throws IOException {
        String contents = "";

        if (parsedRequest[0].equals("GET") && parsedRequest[1].equals("/")) {
            contents = "Hello world!";
        }
        if (parsedRequest[0].equals("GET") && !parsedRequest[1].equals("/")) {
            final URL resource = getClass().getClassLoader().getResource("static" + parsedRequest[1]);
            contents = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }
        return contents;
    }

    private String[] getParsedRequest(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final StringBuilder stringBuilder = new StringBuilder();

        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine()).append("\r\n");
        }
        String request = stringBuilder.toString();
        String[] parsedRequest = request.split(" ");
        return parsedRequest;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
