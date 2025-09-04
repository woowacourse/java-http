package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
             final var outputStream = connection.getOutputStream()) {
            // 요청 url 읽어서 어떤 요청인지 확인
            Request request = parseRequest(inputStream);
            // 요청 url의 자원이 있는지 확인
            if (!hasResource(request.method())) {
                // 없으면? 404 응답 반환
                // 404 페이지 만들어서 가져오기
            }
            // 있으면? 해당 자원 읽어서 문자열로 반환

            //                final var response = String.join("\r\n",
//                        "HTTP/1.1 404 BAD_REQUEST ",
//                        "Content-Type: text/html;charset=utf-8 ",
//                        "Content-Length: " + responseBody.getBytes().length + " ",
//                        "");
//
//                outputStream.write(response.getBytes());
//                outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validateLine(String line) {
        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    private Request parseRequest(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = br.readLine();
            validateLine(line);
            String[] firstLine = line.split(" ");
            if (firstLine.length != 3) {
                throw new IllegalArgumentException();
            }
            String method = firstLine[0];
            String path = firstLine[1];
            String version = firstLine[2];

            Map<String, String> headers = new HashMap<>();
            while ((line = br.readLine()) != null) {
                validateLine(line);
                String[] splitLine = line.split(":");
                if (splitLine.length >= 2) {
                    String key = splitLine[0].trim();
                    StringBuilder sb = new StringBuilder();
                    for (int idx = 1; idx < splitLine.length; idx++) {
                        sb.append(splitLine[idx]);
                    }
                    String value = sb.toString().trim();
                    headers.put(key, value);
                }
            }
            return new Request(method, path, version, headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasResource(String requestUrl) {
        return false;
    }
}
