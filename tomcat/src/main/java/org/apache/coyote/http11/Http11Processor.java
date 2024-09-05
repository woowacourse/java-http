package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_ACCEPT = "Accept";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";


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
             final var outputStream = connection.getOutputStream()) {
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

            String[] httpRequest = input.readLine().split(" ");
            Map<String, String> requestQueries = getQueries(httpRequest);
            if (!requestQueries.isEmpty()) {
                int queryIndex = httpRequest[1].indexOf("?");
                httpRequest[1] = httpRequest[1].substring(0, queryIndex);
            }
            Map<String, String> httpHeaders = getHttpHeaders(input);

            String accept = httpHeaders.get(HEADER_ACCEPT);
            if (accept.contains("text/css")) {
                final String responseBody = getFileValue(httpRequest, accept);
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
            } else {
                final String responseBody = getFileValue(httpRequest, accept);
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                if (!requestQueries.isEmpty()) {
                    logUserExist(requestQueries);
                }
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getQueries(String[] httpRequest) {
        Map<String, String> requestQueries = new HashMap<>();
        if (httpRequest[1].contains("?")) {
            int queryStartIndex = httpRequest[1].indexOf("?") + 1;
            String requestQueryString = httpRequest[1].substring(queryStartIndex);
            String[] queries = requestQueryString.split("&");

            for (String query : queries) {
                String[] q = query.split("=");
                String key = q[0];
                String value = q[1];

                requestQueries.put(key, value);
            }
        }

        return requestQueries;
    }

    private Map<String, String> getHttpHeaders(BufferedReader input) throws IOException {
        Map<String, String> httpHeader = new HashMap<>();

        String line = input.readLine();
        while (!line.equals("")) {
            String[] header = line.split(": ");
            httpHeader.put(header[0], header[1]);

            line = input.readLine();
        }

        return httpHeader;
    }

    private String getFileValue(String[] httpRequest, String accept) throws IOException {
        if (!httpRequest[1].contains(".")) {
            if (accept.contains("text/html")) {
                httpRequest[1] = httpRequest[1] + ".html";
            } else if (accept.contains("text/css")) {
                httpRequest[1] = httpRequest[1] + ".css";
            } else {
                log.warn("'{}' Accept 타입을 처리해주세요", accept);
            }
        }
        String resourcePath = "static" + httpRequest[1];
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        final Path path = new File(resource.getPath()).toPath();

        final String responseBody = Files.readString(path);
        return responseBody;
    }

    private void logUserExist(Map<String, String> requestQueries) {
        String account = "";
        String password = "";
        for (Entry<String, String> entry : requestQueries.entrySet()) {
            if (entry.getKey().equals(ACCOUNT)) {
                account = entry.getValue();
            } else if (entry.getKey().equals(PASSWORD)) {
                password = entry.getValue();
            }
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("찾으시는 유저가 존재하지 않습니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("찾으시는 유저가 존재하지 않습니다.");
        }

        log.info(user.toString());
    }
}
