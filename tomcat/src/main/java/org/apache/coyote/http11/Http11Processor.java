package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.common.StaticResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var request = Request.from(inputStream);

            final var response = makeResponse(request);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse makeResponse(final Request request) throws IOException {
        if (request.getMethod().equals(HttpMethod.GET)) {
            return doGet(request);
        }
        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }

    private HttpResponse doGet(final Request request) throws IOException {
        final var path = request.getPath();
        if (path.equals("/")) {
            return HttpResponse.withStaticResource(new StaticResource("Hello world!", ContentType.TEXT_HTML));
        }
        return HttpResponse.withStaticResource(StaticResource.path(path));
    }
}
