package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.startup.WAS;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
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
        try (var reader = new HttpReader(connection.getInputStream());
             var writer = new HttpWriter(connection.getOutputStream())) {

            HttpRequest request = new HttpRequest(reader.readLines());
            HttpResponse response = WAS.dispatch(request);
            writer.flushWith(response.normalize());

            log.info("request = {}", request);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
