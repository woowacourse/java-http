package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.http11.data.HttpVersion;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.http11.parser.HttpResponseParser;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);
            HttpResponse httpResponse = requestResponse(httpRequest);

            String response = HttpResponseParser.parse(httpResponse);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse requestResponse(HttpRequest request) throws IOException {
        String sessionId = request.getSessionId();
        if (sessionId != null && SessionManager.findSession(sessionId) == null) {
            return new HttpResponse(HttpVersion.HTTP_1_1)
                    .addHttpStatusCode(HttpStatusCode.FOUND)
                    .addRedirectUrl("/login.html");
        }
        Controller controller = RequestMapping.getHandler(request);
        if (controller == null) {
            return new HttpResponse(HttpVersion.HTTP_1_1)
                    .addHttpStatusCode(HttpStatusCode.NOT_FOUND);
        }
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        controller.service(request, response);
        return response;
    }
}
