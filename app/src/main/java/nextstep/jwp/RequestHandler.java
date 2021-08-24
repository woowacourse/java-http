package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = br.readLine();
            log.debug("request line : {}", line);

            if (line == null) {
                return;
            }

            String uri = line.split(" ")[1];

            while (!Objects.equals(line, "")) {
                line = br.readLine();
                log.debug("header : {}", line);
            }

            if (uri.startsWith("/login")) {
                int index = uri.indexOf("?");
                String queryString = uri.substring(index + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
                Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
                user.ifPresent(u -> log.debug("{} 유저가 존재", u.getAccount()));
            } else {
                URL resource = getClass().getClassLoader().getResource("static/" + uri);
                String file = Objects.requireNonNull(resource).getFile();
                Path path = new File(file).toPath();
                String responseBody = new String(Files.readAllBytes(path));

                final String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
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
