package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.executor.LoginExecutor;
import com.techcourse.executor.PageExecutor;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<Executor> executors;
    private final Executor pageExecutor = new PageExecutor();


    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.executors = List.of(new LoginExecutor());
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

            final HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

            final HttpResponse response = executors.stream()
                    .filter(executor -> executor.isMatch(httpRequest))
                    .findFirst()
                    .map(executor -> executor.execute(httpRequest))
                    .orElseGet(() -> pageExecutor.execute(httpRequest));

            HttpResponseWriter.write(outputStream, response);
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
