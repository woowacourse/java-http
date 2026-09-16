package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String START_LINE_DELIMITER = " ";
    private static final char HEADER_DELIMITER = ':';
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            String[] startLineTokens = bufferedReader.readLine().split(START_LINE_DELIMITER);
            String httpUrl = startLineTokens[1];

            Map<String, String> headers = parseHeaders(bufferedReader);

            if (httpUrl.startsWith("/index.html")) {
                final String body = readFile("static/index.html");
                final var response = createResponse(body, headers);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/login")) {
                final String body = readFile("static/login.html");
                final var response = createResponse(body, headers);

                outputStream.write(response.getBytes());
                outputStream.flush();

                Map<String, String> queryParams = parseQueryParams(startLineTokens);

                String account = queryParams.get("account");
                String password = queryParams.get("password");

                InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.checkPassword(password))
                        .ifPresent(user -> log.info("{}", user));
                return;
            }

            if (httpUrl.startsWith("/css/styles.css")) {
                final String body = readFile("static/css/styles.css");
                final var response = createResponse(body, headers);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/assets/chart-bar.js")) {
                final String body = readFile("static/assets/chart-bar.js");
                final var response = createResponse(body, headers);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/js/scripts.js")) {
                final String body = readFile("static/js/scripts.js");
                final var response = createResponse(body, headers);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/assets/chart-pie.js")) {
                final String body = readFile("static/assets/chart-pie.js");
                final var response = createResponse(body, headers);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final var response = createResponse("Hello world!", headers);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        // 각 요청을 처리하는 스레드는 개별적인 http11Processor 인스턴스를 가지므로, 동시성을 보장하는 자료구조를 사용하지 않았습니다.
        Map<String, String> headers = new HashMap<>();
        String nextLine = null;
        while ((nextLine = bufferedReader.readLine()) != null) {
            // Host에 콜론(:)이 포함될 수 있으므로 가장 먼저 만나는 콜론을 기준으로 나눕니다.
            int headerDelimiterIndex = nextLine.indexOf(HEADER_DELIMITER);
            String key = nextLine.substring(0, headerDelimiterIndex);
            String value = nextLine.substring(headerDelimiterIndex + 1);

            headers.put(key, value);
        }
        return headers;
    }

    private String createResponse(String responseBody, Map<String, String> headers) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                CONTENT_TYPE + ": " + headers.get(CONTENT_TYPE) + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String readFile(String path) throws IOException {
        URL url = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }

    private Map<String, String> parseQueryParams(String[] queryParamLine) {
        int queryStringDelimiterIndex = queryParamLine[1].lastIndexOf(QUERY_STRING_DELIMITER);

        String queryLine = queryParamLine[1].substring(queryStringDelimiterIndex + 1);
        String[] params = queryLine.split(PARAM_DELIMITER);

        Map<String, String> queries = new HashMap<>();
        for (String param : params) {
            String[] keyToken = param.split(KEY_VALUE_DELIMITER);
            queries.put(keyToken[0], keyToken[1]);
        }

        return queries;
    }
}
