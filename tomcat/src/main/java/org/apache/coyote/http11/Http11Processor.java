package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
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
            Handler handler = RequestMapper.getHandler(httpRequest);

            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1)
                    .addHttpStatusCode(HttpStatusCode.NOT_FOUND);
            if (handler != null) {
                httpResponse = handler.doHandle(httpRequest, httpResponse); // null 처리
            }

            String response = HttpResponseParser.parse(httpResponse);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
