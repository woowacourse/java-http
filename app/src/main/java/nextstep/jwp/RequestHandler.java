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

            String requestHttpMethod = requestLine.split(" ")[0];
            String requestUri = requestLine.split(" ")[1];

            final StringBuilder stringBuilder = new StringBuilder();


            String line = null;
            do {
                line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                stringBuilder.append(line).append("\r\n");
                log.debug(line);
            } while (!"".equals(line));


            if ("POST".equals(requestHttpMethod)) {
                HttpHeaders httpHeaders = new HttpHeaders(stringBuilder.toString());
                int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);


                if ("/register".equals(requestUri)) {
                    String[] splitBody = requestBody.split("&");
                    String account = splitBody[0].split("=")[1];
                    String password = splitBody[1].split("=")[1];
                    String email = splitBody[2].split("=")[1];

                    User user = new User(2, account, password, email);
                    InMemoryUserRepository.save(user);

                    String response = createViewResponse("/index.html", "302 Found");
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                }

                if ("/login".equals(requestUri)) {
                    String[] splitBody = requestBody.split("&");
                    String account = splitBody[0].split("=")[1];
                    String password = splitBody[1].split("=")[1];

                    Optional<User> user = InMemoryUserRepository.findByAccount(account);
                    log.debug(user.toString());

                    if (user.isEmpty()) {
                        String failureResponse = createViewResponse("/401.html", "302 Found");
                        outputStream.write(failureResponse.getBytes());
                        outputStream.flush();
                        return;
                    }

                    if (!user.get().checkPassword(password)) {
                        String failureResponse = createViewResponse("/401.html", "302 Found");
                        outputStream.write(failureResponse.getBytes());
                        outputStream.flush();
                        return;
                    }

                    String successResponse = createViewResponse("/index.html", "302 Found");
                    outputStream.write(successResponse.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            if ("GET".equals(requestHttpMethod)) {

                if ("/register".equals(requestUri)) {
                    String response = createViewResponse("/register.html", "302 Found");
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }

                if ("/login".equals(requestUri)) {
                    requestUri += ".html";
                    String response = createViewResponse(requestUri, "200 OK");
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }

                String response = createViewResponse("/index.html", "200 OK");
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String createViewResponse(String viewName, String statusCode) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + viewName);
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
