package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.request.HttpRequestParser;
import org.apache.coyote.request.MalformedRequestException;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.request.UnknownMethodException;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
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
            MyHttpRequest httpRequest;
            try {
                httpRequest = HttpRequestParser.parse(readHttpRequest(
                        new BufferedReader(new InputStreamReader(inputStream))));
            } catch (UnknownMethodException e) {
                writeErrorResponse(outputStream, StatusCode.NOT_IMPLEMENTED);
                return;
            } catch (MalformedRequestException e) {
                writeErrorResponse(outputStream, StatusCode.BAD_REQUEST);
                return;
            }
            MyHttpResponse httpResponse = new MyHttpResponse();
            log.info("start request: {} {}", httpRequest.method(), httpRequest.getUri());

            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            if (httpRequest.isNewSession()) {
                httpResponse.addHeader(
                        "Set-Cookie",
                        "JSESSIONID=" + httpRequest.getSession(false).getId()
                );
            }

            writeResponse(outputStream, httpResponse);
            log.info("end request: {} {}", httpRequest.method(), httpRequest.getUri());
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeResponse(final OutputStream outputStream,
                                      final MyHttpResponse response) throws IOException {
        outputStream.write(response.build().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static void writeErrorResponse(final OutputStream outputStream,
                                           final StatusCode statusCode) throws IOException {
        MyHttpResponse response = new MyHttpResponse();
        response.setStatusCode(statusCode);
        writeResponse(outputStream, response);
    }

    private static String readHttpRequest(BufferedReader br) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int contentLength = 0;
        while (true) {
            String line = br.readLine();
            if (line == null) {
                throw new MalformedRequestException("HTTP 요청이 완성되지 않았습니다.");
            }
            if (line.isEmpty()) {
                break;
            }
            sb.append(line).append("\r\n");
            if (line.regionMatches(true, 0, "Content-Length:", 0, "Content-Length:".length())) {
                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).strip());
            }
        }
        sb.append("\r\n");

        char[] cbuf = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int count = br.read(cbuf, read, contentLength - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        sb.append(cbuf, 0, read);
        return sb.toString();
    }
}
