package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping();

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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return;
            }

            RequestLine requestLine = new RequestLine(readLine);

            Map<String, String> readHttpRequestHeaders = readHeaders(bufferedReader);
            String readRequestBody = readRequestBody(bufferedReader, readHttpRequestHeaders);

            HttpRequest httpRequest = new HttpRequest(requestLine, readHttpRequestHeaders, readRequestBody);
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1", 200, "OK", "");
            Controller controller = requestMapping.getController(httpRequest);

            controller.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.toHttpMessage().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException e) {
            log.error("HTTP 요청 처리 중 입출력 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("HTTP 요청 처리 중 예상하지 못한 오류가 발생했습니다.", e);
        }
    }

    private Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine;

        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
            int separatorIndex = headerLine.indexOf(":");

            if (separatorIndex == -1) {
                continue;
            }

            String headerName = headerLine
                    .substring(0, separatorIndex)
                    .trim()
                    .toLowerCase(Locale.ROOT);

            String headerValue = headerLine
                    .substring(separatorIndex + 1)
                    .trim();

            headers.put(headerName, headerValue);
        }

        return headers;
    }

    private String readRequestBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        String contentLengthValue = headers.get("content-length");

        if (contentLengthValue == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];

        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = bufferedReader.read(buffer, totalRead, contentLength - totalRead);

            if (read == -1) {
                break;
            }

            totalRead += read;
        }

        return new String(buffer, 0, totalRead);
    }
}
