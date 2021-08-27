package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
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
import java.util.Optional;

public class RequestHandler implements Runnable {

    public static final String DEFAULT_METHOD = "Hello world!";
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String STATIC_PATH = "static";
    private static final String HEADER_DELIMITER = ": ";

    private static long user_id = 2;

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
            final String firstLine = bufferedReader.readLine();
            final Map<String, String> httpRequestHeaders = httpRequestHeaders(bufferedReader);
            final String extractedUri = extractUri(firstLine);
            final String extractedMethod = extractHttpMethod(firstLine);

            if ("/".equals(extractedUri)) {
                final String response = http200Message(DEFAULT_METHOD);
                writeOutputStream(outputStream, response);
                return;
            }

            if (extractedUri.startsWith("/login")) {
                if ("POST".equals(extractedMethod)) {
                    try {
                        final String requestBody = extractRequestBody(bufferedReader, httpRequestHeaders);
                        loginRequest(requestBody);
                        writeOutputStream(outputStream, http302Response("/index.html"));
                    } catch (RuntimeException exception) {
                        writeOutputStream(outputStream, http302Response("/401.html"));
                    }
                    return;
                }
                writeOutputStream(outputStream, httpHtmlResponse(STATIC_PATH, extractedUri));
                return;
            }

            if (extractedUri.startsWith("/register")) {
                if ("POST".equals(extractedMethod)) {
                    final String requestBody = extractRequestBody(bufferedReader, httpRequestHeaders);
                    registerRequest(requestBody);
                    writeOutputStream(outputStream, http302Response("/index.html"));
                    return;
                }
                writeOutputStream(outputStream, httpHtmlResponse(STATIC_PATH, extractedUri));
                return;
            }

            if ("/css/styles.css".equals(extractedUri)) {
                final String response = httpCssMessage();
                writeOutputStream(outputStream, response);
            }

            writeOutputStream(outputStream, httpHtmlResponse(STATIC_PATH, extractedUri));
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Map<String, String> httpRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> httpRequestHeaders = new HashMap<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            if (Objects.isNull(line)) {
                break;
            }
            if ("".equals(line)) {
                break;
            }
            final String[] header = line.split(HEADER_DELIMITER);
            httpRequestHeaders.put(header[0], header[1].strip());
        }
        return httpRequestHeaders;
    }

    private String extractUri(String firstLine) {
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[1];
    }

    private String extractHttpMethod(String firstLine) {
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[0];
    }

    private String extractRequestBody(BufferedReader bufferedReader, Map<String, String> httpRequestHeaders) throws IOException {
        final int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private String loginRequest(String requestBody) {
        Map<String, String> params = extractQueryParam(requestBody);
        final User user = findUserByAccount(params);
        if (user.checkPassword(params.get("password"))) {
            return user.toString();
        }
        throw new IllegalArgumentException("옳지 않은 비밀번호입니다.");
    }

    private User findUserByAccount(Map<String, String> params) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        if (user.isPresent()) {
            return user.get();
        }
        throw new IllegalArgumentException("찾을 수 없는 사용자입니다.");
    }

    private void registerRequest(String requestBody) {
        Map<String, String> params = extractQueryParam(requestBody);
        final User user = new User(user_id++, params.get("account"), params.get("password"), params.get("email"));
        InMemoryUserRepository.save(user);
    }

    private Map<String, String> extractQueryParam(String queryParameterString) {
        Map<String, String> params = new HashMap<>();
        final String[] parameters = queryParameterString.split("&");
        for (String parameter : parameters) {
            final String[] splitParameter = parameter.split("=");
            params.put(splitParameter[0], splitParameter[1]);
        }
        return params;
    }

    private void writeOutputStream(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String httpCssMessage() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css ",
                "",
                "");
    }

    private String http200Message(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String http302Response(String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080" + redirectUrl);
    }

    private String httpHtmlResponse(String resourcesPath, String targetUri) throws IOException {
        if (!targetUri.contains(".") && !targetUri.contains(".html")) {
            targetUri = targetUri.concat(".html");
        }
        final URL uri = getClass().getClassLoader().getResource(resourcesPath + targetUri);
        return http200Message(Files.readString(new File(uri.getFile()).toPath()));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
