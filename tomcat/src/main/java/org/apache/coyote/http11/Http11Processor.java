package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 소켓의 InputStream으로 클라이언트가 보낸 HTTP 요청을 읽고,
 * 요청을 처리할 Controller를 찾아 실행한 뒤 OutputStream으로 HTTP 응답을 전송한다.
 *
 * 요청을 해석하는 책임은 HttpRequest가, 응답을 HTTP 형식으로 만드는 책임은 HttpResponse가,
 * URI와 HTTP 메서드에 따른 실제 처리 책임은 각 Controller가 담당한다.
 */
public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

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
             final var bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HttpRequest request = parseHttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse();

            addSessionCookie(request, response);

            Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);

            sendResponse(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionCookie(HttpRequest request, HttpResponse response) {
        HttpCookie httpCookie = request.getHttpCookie();
        if (!httpCookie.hasJsessionId()) {
            String sessionId = UUID.randomUUID().toString();
            Session session = new Session(sessionId);
            SessionManager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
        }
    }

    private HttpRequest parseHttpRequest(BufferedReader bufferedReader) throws IOException {
        StringBuilder rawRequest = new StringBuilder();

        String requestStartLine = bufferedReader.readLine();
        if (requestStartLine == null) {
            throw new IOException("HTTP 요청의 시작 줄을 읽을 수 없습니다.");
        }
        rawRequest.append(requestStartLine).append("\r\n");

        String line;
        int contentLength = 0;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            rawRequest.append(line).append("\r\n");
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(
                        line.substring("Content-Length:".length()).trim()
                );
            }
        }

        rawRequest.append("\r\n");
        rawRequest.append(getRequestBody(contentLength, bufferedReader));

        return new HttpRequest(rawRequest.toString());
    }

    private String getRequestBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] requestBodyChars = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int count = bufferedReader.read(
                    requestBodyChars,
                    totalRead,
                    contentLength - totalRead
            );
            if (count == -1) {
                break;
            }
            totalRead += count;
        }

        return new String(requestBodyChars, 0, totalRead);
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toResponse().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
