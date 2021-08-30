package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final Map<String, String> httpRequestHeaders = new HashMap<>();
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            final String[] request = line.split(" ");
            final String method = request[0];
            final String uri = request[1];
            byte[] body = new byte[0];
            HttpStatus status = HttpStatus.OK;
            String location = null;

            if ("/".equals(uri)) {
                body =Files.readAllBytes(getResources("/index.html").toPath());
                String response = makeResponse(ContentType.HTML.getType(), HttpStatus.OK, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.endsWith(".html")) {
                body =Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.HTML.getType(), HttpStatus.OK, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.endsWith(".css")) {
                body =Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.CSS.getType(), HttpStatus.OK, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.endsWith(".js")) {
                body =Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.JS.getType(), HttpStatus.OK, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.startsWith("/assets/img")) {
                body =Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.IMAGE.getType(), HttpStatus.OK, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if ("".equals(line)) {
                    break;
                }
                String[] headers = line.split(": ");
                httpRequestHeaders.put(headers[0], headers[1]);
            }

            String fileName = uri;
            String requestBody = null;

            if ("POST".equals(method)) {
                String rawLength = httpRequestHeaders.get("Content-Length");
                if (!Objects.isNull(rawLength)) {
                    int length = Integer.parseInt(rawLength);
                    char[] buffer = new char[length];
                    bufferedReader.read(buffer, 0, length);
                    requestBody = new String(buffer);
                }
            }

            if (uri.startsWith("/login")) {
                fileName = "/login.html";

                if (!Objects.isNull(requestBody)) {
                    String[] rawData = requestBody.split("&");
                    String account = getDataFromQueryString(rawData[0]);
                    String password = getDataFromQueryString(rawData[1]);

                    User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
                    if (user.checkPassword(password)) {
                        fileName = "/index.html";
                        status = HttpStatus.FOUND;
                        location = "/index.html";
                    } else {
                        fileName = "/401.html";
                        status = HttpStatus.FOUND;
                        location = "/401.html";
                    }
                }
            }

            if (uri.startsWith("/register")) {
                fileName = "/register.html";

                if (!Objects.isNull(requestBody)) {
                    String[] rawData = requestBody.split("&");
                    String account = getDataFromQueryString(rawData[0]);
                    String email = getDataFromQueryString(rawData[1]);
                    String password = getDataFromQueryString(rawData[2]);
                    InMemoryUserRepository.save(new User(2L, account, password, email));
                    fileName = "/login.html";
                    status = HttpStatus.FOUND;
                    location = "/login.html";
                }
            }

            body = Files.readAllBytes(getResources(fileName).toPath());
            final String response = makeResponse(ContentType.HTML.getType(), status, location, body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String makeResponse(String contentType, HttpStatus status, String location, byte[] body) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.getStatus(),
                "Content-Type: " + contentType,
                "Content-Length: " + body.length + " ",
                "Location: " + location,
                "",
                new String(body));
    }

    private String getDataFromQueryString(String data) {
        return data.substring(data.indexOf("=") + 1);
    }

    private File getResources(String fileName) {
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        if (resource != null) {
            return new File(resource.getPath());
        }
        return getResources("/404.html");
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
