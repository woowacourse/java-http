package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (bufferedReader.ready()) {
                Map<RequestLineElement, String> requestLineElements = extractRequestLine(bufferedReader);
                URL resource = getClass().getClassLoader()
                        .getResource("static" + requestLineElements.get(RequestLineElement.REQUEST_URI));
                if (requestLineElements.get(RequestLineElement.REQUEST_URI).startsWith("/login")) {
                    parseLogin(requestLineElements.get(RequestLineElement.REQUEST_URI));
                    resource = getClass().getClassLoader().getResource("static/login.html");
                }

                Map<String, String> headers = parseHeaders(bufferedReader);
                String contentType = convertContentTypeByAccept(headers.get("Accept"));

                if (isResourceExists(resource)) {
                    final String content = new String(Files.readAllBytes(new File((resource).getFile()).toPath()));
                    rendering(content, contentType, outputStream);
                    return;
                }
                rendering(requestLineElements.get(RequestLineElement.REQUEST_URI), contentType, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static boolean isResourceExists(URL resource) {
        return !Objects.isNull(resource);
    }

    private static void rendering(String content, String contentType, OutputStream outputStream) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static Map<RequestLineElement, String> extractRequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        String[] requestLineElements = line.split(" ");
        String requestURI = convertToDefaultURI(requestLineElements[1]);
        return Map.of(RequestLineElement.HTTP_METHOD, requestLineElements[0],
                RequestLineElement.REQUEST_URI, requestURI,
                RequestLineElement.HTTP_VERSION, requestLineElements[2]);
    }

    private static String convertToDefaultURI(String uri) {
        if (uri.equals("/")) {
            return "/index.html";
        }
        return uri;
    }

    private static Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(line, ":");
            String key = tokenizer.nextToken().trim();
            String value = tokenizer.nextToken("").trim();
            headerMap.put(key, value);
            line = bufferedReader.readLine();
        }
        return headerMap;
    }

    private static String convertContentTypeByAccept(String accept) {
        if (!Objects.isNull(accept) && accept.contains("text/css")) {
            return "text/css";
        }
        return "text/html";
    }

    private static void parseLogin(String uri) {
        if (!(uri.contains("?account=") && uri.contains("&password="))) {
            return;
        }
        String queryString = uri.substring("/login?".length());
        int index = queryString.indexOf("&");
        String account = queryString.substring("account=".length(), index);
        String password = queryString.substring(index + 1 + "password=".length());
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> {
                            log.error("account: {} 는 존재하지 않는 사용자입니다.", account);
                            return new RuntimeException();
                        }
                );
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            return;
        }
        log.error("user: {}, inputPassword={}, 비밀번호가 올바르지 않습니다.", user, password);
        throw new RuntimeException();
    }
}
