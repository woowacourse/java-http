package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestParser;
import org.apache.coyote.http.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.HttpServlet;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final RequestParser requestParser;

    private final HttpServlet httpServlet;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestParser = RequestParser.getInstance();
        this.httpServlet = HttpServlet.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream();
             var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             var bufferedReader = new BufferedReader(inputStreamReader)) {

            Request request = requestParser.parse(bufferedReader);
            Response response = new Response();
            httpServlet.service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }
}
