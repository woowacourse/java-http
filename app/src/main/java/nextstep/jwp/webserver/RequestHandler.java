package nextstep.jwp.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(new String(inputStream.readAllBytes()));

            Controller controller = Router.get(httpRequest.getUri());
            String responseBody = handle(controller, httpRequest);

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

    private String handle(Controller controller, HttpRequest httpRequest) {
        if (controller == null) {
            return respondStaticFile(httpRequest.getUri());
        }
        return controller.handle(httpRequest);
    }

    private String respondStaticFile(String uriPath) {
        String[] paths = uriPath.split("/");
        String fileName = paths[paths.length - 1];
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);

        if (resource == null) {
            throw new RuntimeException(); // 파일없음
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "파일 read 중 에러 발생";
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
