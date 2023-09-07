package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.adapter.LoginAdapter;
import org.apache.coyote.adapter.RegisterAdapter;
import org.apache.coyote.adapter.StringAdapter;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestParser;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;
import org.apache.coyote.view.Resource;
import org.apache.coyote.view.ResponseResolver;
import org.apache.coyote.view.ViewResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = new BufferedOutputStream(connection.getOutputStream())) {
            RequestParser requestParser = new RequestParser(inputStream);
            Request request = requestParser.parse();

            Response response = doHandler(request);

            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response doHandler(Request request) {
        if (request.isSamePath("/")) {
            Resource resource = new StringAdapter().adapt(request);
            return responseResolver.resolve(request, resource);
        }
        if (request.isSamePath("/login")) {
            Resource resource = new LoginAdapter().adapt(request);
            return responseResolver.resolve(request, resource);
        }
        if (request.isSamePath("/register")) {
            Resource resource = new RegisterAdapter().adapt(request);
            return responseResolver.resolve(request, resource);
        }
        return responseResolver.resolve(request,
                ViewResource.of(request.getPath(), HttpStatus.OK, HttpCookie.from("")));
    }
}
