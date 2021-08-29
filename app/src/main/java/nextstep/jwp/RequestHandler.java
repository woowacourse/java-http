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
             final OutputStream outputStream = connection.getOutputStream()) {

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String firstLine = bufferedReader.readLine();
            String requestUri = firstLine.split(" ")[1];

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                System.out.println(line);
            }

            if ("/login".equals(requestUri)) {
                requestUri += ".html";
            }

            if (requestUri.contains("/login?")) {
                int index = requestUri.indexOf("?");
                String path = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);

                int index2 = queryString.indexOf("&");
                String account = queryString.substring(0, index2).split("=")[1];
                String password = queryString.substring(index2 + 1).split("=")[1];

                User user = InMemoryUserRepository.findByAccount(account).orElseThrow(IllegalArgumentException::new);

                if (!user.checkPassword(password)) {
                    throw new IllegalArgumentException("올바르지 않은 비밀번호입니다.");
                }

                log.debug(user.toString());
                return;
            }

            final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
            final Path path = new File(resource.getPath()).toPath();
            final List<String> lines = Files.readAllLines(path);

            String result = lines.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining());


            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + result.getBytes().length + " ",
                    "",
                    result);

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
