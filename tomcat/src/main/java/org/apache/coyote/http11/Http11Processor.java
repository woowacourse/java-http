package org.apache.coyote.http11;

import static org.apache.coyote.http11.data.SessionManager.JSESSIONID_COOKIE_NAME;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.config.TomcatServerConfiguration;
import org.apache.coyote.http11.data.Cookie;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.apache.coyote.http11.data.Session;
import org.apache.coyote.http11.resolver.RequestResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<RequestResolver> requestResolvers;

    public Http11Processor(final Socket connection) {
        this(connection, TomcatServerConfiguration.requestResolvers);
    }

    public Http11Processor(final Socket connection, final List<RequestResolver> requestResolvers) {
        this.connection = connection;
        this.requestResolvers = requestResolvers;
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

            final Request request = Request.from(inputStream);
            final Response response = handleRequest(request);

            final Session session = request.getSession(false);
            if (session != null) {
                Cookie cookie = Cookie.create(JSESSIONID_COOKIE_NAME, session.getId(), "/");
                response.addCookie(cookie);
            }

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response handleRequest(Request request) {
        for (RequestResolver requestResolver : requestResolvers) {
            if (requestResolver.canHandle(request)) {
                return requestResolver.handleRequest(request);
            }
        }

        return Response.badRequest();
    }
}
