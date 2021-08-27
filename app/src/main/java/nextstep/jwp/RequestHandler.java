package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.HttpRequestUtils;
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

            String response = "";
            if (uri.contains(".")) { // 뭔가 정적 파일인 경우
                Path path = getPath(uri);
                String responseBody = new String(Files.readAllBytes(path));
                response = responseHeaderOfStatusOK(responseBody);
                writeBody(outputStream, response);
                return;
            }

            if (uri.startsWith("/login")) {
                int index = uri.indexOf("?");
                if (index == -1) {
                    Path path = getPath("login.html");
                    String responseBody = new String(Files.readAllBytes(path));
                    response = responseHeaderOfStatusOK(responseBody);
                    writeBody(outputStream, response);
                    return;
                }

                String queryString = uri.substring(index + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
                Optional<User> findUser = InMemoryUserRepository.findByAccount(params.get("account"))
                        .filter(user -> user.checkPassword(params.get("password")))
                        .stream().findAny();

                if (findUser.isEmpty()) {
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /401.html",
                            "");
                    writeBody(outputStream, response);
                    return;
                }
                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html",
                        "");
                writeBody(outputStream, response);
            }
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Path getPath(String uri) {
        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        String file = Objects.requireNonNull(resource).getFile();
        Path path = new File(file).toPath();
        return path;
    }

    private void writeBody(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String responseHeaderOfStatusOK(String body) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
