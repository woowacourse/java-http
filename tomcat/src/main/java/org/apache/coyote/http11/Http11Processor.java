package org.apache.coyote.http11;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Socket connection;
    private final RequestHandler requestHandler;

    public Http11Processor(final Socket connection, final RequestHandler requestHandler) {
        this.connection = connection;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpResponse httpResponse = createResponse(inputStream);

            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (EOFException e) {
            log.info("클라이언트가 요청 없이 연결을 닫았습니다.");
        } catch (IOException e) {
            log.error("연결에 응답하지 못했습니다.", e);
        } catch (RuntimeException e) {
            log.error("응답을 전송하지 못했습니다.", e);
        }
    }

    private HttpResponse createResponse(final InputStream inputStream) throws EOFException {
        try {
            final HttpRequest httpRequest = HttpRequest.from(inputStream);
            log.info("{} {}", httpRequest.requestLine().method(), httpRequest.requestLine().path());

            return requestHandler.handle(httpRequest);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 요청입니다. {}", e.getMessage());

            return statusOnly(new HttpStatusLine(HTTP_VERSION, 400, "Bad Request"));
        } catch (EOFException e) {
            throw e;
        } catch (Exception e) {
            log.error("응답을 만들지 못했습니다.", e);

            return statusOnly(new HttpStatusLine(HTTP_VERSION, 500, "Internal Server Error"));
        }
    }

    private HttpResponse statusOnly(final HttpStatusLine statusLine) {
        return new HttpResponse(statusLine, Map.of(CONTENT_LENGTH, "0"), new byte[0]);
    }
}
