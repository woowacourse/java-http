package nextstep.jwp;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            String requestLine = bufferedReader.readLine();
            String requestUri = requestLine.split(" ")[1];

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                log.debug(line);
            }

            if ("/login".equals(requestUri)) {
                requestUri += ".html";
                String response = createResponse(requestUri, "200 OK");
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (requestUri.contains("/login?")) {
                int index = requestUri.indexOf("?");
                String path = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);

                int index2 = queryString.indexOf("&");
                String account = queryString.substring(0, index2).split("=")[1];
                String password = queryString.substring(index2 + 1).split("=")[1];

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                log.debug(user.toString());

                if (user.isEmpty()) {
                    String failureResponse = createResponse("/401.html", "302 Found");
                    outputStream.write(failureResponse.getBytes());
                    outputStream.flush();
                    return;
                }

                if (!user.get().checkPassword(password)) {
                    String failureResponse = createResponse("/401.html", "302 Found");
                    outputStream.write(failureResponse.getBytes());
                    outputStream.flush();
                    return;
                }

                String successResponse = createResponse("/index.html", "302 Found");
                outputStream.write(successResponse.getBytes());
                outputStream.flush();
                return;
            }

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String createResponse(String requestUri, String statusCode) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());


        final String response = String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "",
                result);
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
