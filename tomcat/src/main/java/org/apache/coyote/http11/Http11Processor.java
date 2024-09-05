package org.apache.coyote.http11;

import com.techcourse.ResponseResolver;
import com.techcourse.exception.UncheckedServletException;
import org.apache.HttpRequest;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResponseResolver responseResolver;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.responseResolver = new ResponseResolver();
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

            HttpRequest httpRequest = HttpRequest.from(inputStream);

            if (!httpRequest.isHttp11VersionRequest()) {
                throw new IOException("not http1.1 request");
            }

//			Session session;
//			if (sessionId == null || sessionManager.findSession(sessionId) == null) {
//				session = sessionManager.createSession();
//			} else {
//				session = sessionManager.findSession(sessionId);
//			}

            var response = responseResolver.processRequest(httpRequest);

            // 세션 ID가 없다면 새로 발급한 세션 ID를 Set-Cookie 헤더에 포함
//			if (sessionId == null) {
//				outputStream.write(("Set-Cookie: JSESSIONID=" + session.getId() + "; Path=/; HttpOnly\r\n").getBytes());
//			}

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
