package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import com.techcourse.servlet.handler.DispatcherServlet;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final DispatcherServlet dispatcherServlet;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.dispatcherServlet = new DispatcherServlet();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequest.from(inputStream);
            log.info("http request : {}", request);
            HttpResponse response = HttpResponse.from(request);

            dispatcherServlet.doDispatch(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException exception) {
            log.error("요청 처리 과정 중 에러 발생 : {}", exception.getMessage());
        }
    }
}
