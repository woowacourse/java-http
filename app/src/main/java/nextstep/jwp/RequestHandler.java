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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream))) {

            final Request request = createdRequest(bufferedReader);

            if (Objects.isNull(request)) {
                return;
            }

            Response response = response(request);

            outputStream.write(Objects.requireNonNull(response).getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Request createdRequest(BufferedReader bufferedReader) throws IOException {
        try {
            String line = bufferedReader.readLine();

            String[] requestUri = line.split(" ");
            HttpMethod httpMethod = HttpMethod.valueOf(requestUri[0]);
            String uri = requestUri[1];
            String httpVersion = requestUri[2];
            Map<String, String> header = getHeader(bufferedReader);
            Map<String, String> body = getBody(bufferedReader, header, httpMethod);

            return new Request.Builder()
                .method(httpMethod)
                .uri(uri)
                .httpVersion(httpVersion)
                .header(header)
                .body(body)
                .build();
        } catch (Exception e) {
            log.debug("Request Error : {}", e.getMessage());
            return null;
        }
    }

    private Map<String, String> getHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();

        String line = bufferedReader.readLine();
        while (!"".equals(line) && !Objects.isNull(line)) {
            int index = line.indexOf(":");
            if (index == -1) {
                continue;
            }
            String key = line.substring(0, index);
            String value = line.substring(index + 2);
            header.put(key, value);
            line = bufferedReader.readLine();
        }
        return header;
    }

    private Map<String, String> getBody(BufferedReader bufferedReader, Map<String, String> header,
        HttpMethod httpMethod) throws IOException {

        Map<String, String> requestBody = new HashMap<>();

        if (httpMethod.isBody() && header.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(header.get("Content-Length"));
            char[] buffer = new char[contentLength];
            int read = bufferedReader.read(buffer, 0, contentLength);
            if (read == -1) {
                return requestBody;
            }
            String line = new String(buffer);
            String[] bodyDates = line.split("&");
            for (String body : bodyDates) {
                String[] keyValue = body.split("=");
                requestBody.put(keyValue[0], keyValue[1]);
            }
        }
        return requestBody;
    }

    private Response response(Request request) throws IOException {
        if (request.isEqualsHttpMethod(HttpMethod.GET)) {
            return doGet(request);
        }
        if (request.isEqualsHttpMethod(HttpMethod.POST)) {
            return doPost(request);
        }
        return null;
    }

    private Response doGet(Request request) throws IOException {
        String uri = request.getUri();

        if (request.isUriMatch("/login") || request.isUriMatch("/register")) {
            String responseBody = getResponseBody("static" + uri + ".html");
            return Response.create200OK(request, responseBody);
        }
        if (request.isUriFile()) {
            String responseBody = getResponseBody("static" + uri);
            return Response.create200OK(request, responseBody);
        }
        return null;
    }

    private Response doPost(Request request) {
        if (request.isUriMatch("/login")) {
            User user = InMemoryUserRepository
                .findByAccount(request.getRequestBody("account"))
                .orElseThrow(IllegalArgumentException::new);
            if (user.checkPassword(request.getRequestBody("password"))) {
                log.debug("{} login success", user.getAccount());
                return Response.create302Found(request, "/index.html");
            }
        }
        if (request.isUriMatch("/register")) {
            String account = request.getRequestBody("account");
            String password = request.getRequestBody("password");
            String email = request.getRequestBody("email");
            User user = new User(0, account, password, email);
            InMemoryUserRepository.save(user);
            log.debug("{} user create success", user.getAccount());
            return Response.create302Found(request, "/index.html");
        }
        return null;
    }

    private String getResponseBody(String uri) throws IOException {
        URL resource = this.getClass().getClassLoader().getResource(uri);

        return new String(Files.readAllBytes(
            new File(Objects.requireNonNull(resource).getFile()).toPath()));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
