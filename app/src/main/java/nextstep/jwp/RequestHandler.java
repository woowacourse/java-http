package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> request = new ArrayList<>();
            while (bufferedReader.ready()) {
                request.add(bufferedReader.readLine());
            }
            String requestURI = request.get(0).split(" ")[1];
            String path;
            URL resource = null;
            if (requestURI.contains(".")) {
                path = "static" + requestURI;
                resource = getClass().getClassLoader().getResource(path);
            }
            if (!requestURI.contains(".") && !requestURI.contains("?")) {
                path = "static" + requestURI + ".html";
                resource = getClass().getClassLoader().getResource(path);
            }

            if (requestURI.contains("?")) {
                int index = requestURI.indexOf("?");
                path = requestURI.substring(0, index);
                String queryString = requestURI.substring(index + 1);

                String[] queries = queryString.split("&");
                List<String> values = new ArrayList<>();
                for (String query : queries) {
                    values.add(query.split("=")[1]);
                }
                User user = InMemoryUserRepository.findByAccount(values.get(0)).orElseThrow();
                log.debug("account : {}, checkPassword : {}", user.getAccount(), user.checkPassword(values.get(1)));
            }


            final Path filePath = new File(resource.getFile()).toPath();
            final String responseBody = Files.readString(filePath);

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

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
