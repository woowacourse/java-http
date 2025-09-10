package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.error.ErrorMapper;
import org.apache.coyote.http11.handler.GreetingHandler;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.handler.HttpResourceHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final QueryParser queryParser;
    private final HttpRequestReader httpRequestReader;
    private final HttpResourceLoader httpResourceLoader;
    private final HttpResourceHandler httpResourceHandler;
    private final HttpResponseWriter httpResponseWriter;
    private final HttpCookie httpCookie;
    private final SessionManager sessionManager;
    private final Resolver resolver;
    private final ErrorMapper errorMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.queryParser = new QueryParser();
        this.httpRequestReader = new HttpRequestReader(queryParser);
        this.httpResourceLoader = new HttpResourceLoader();
        this.httpResourceHandler = new HttpResourceHandler(httpResourceLoader);
        this.httpResponseWriter = new HttpResponseWriter();
        this.httpCookie = new HttpCookie();
        this.sessionManager = new SessionManager();
        this.resolver = new Resolver(httpResourceHandler)
                .register("/", new GreetingHandler())
                .register("/login", new LoginHandler(httpResourceLoader, queryParser, httpCookie, sessionManager))
                .register("/register", new RegisterHandler(httpResourceLoader, queryParser))
        ;
        this.errorMapper = new ErrorMapper(httpResourceLoader);
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
            HttpRequest httpRequest = httpRequestReader.read(inputStream);
            String path = httpRequest.path();

            HttpHandler handler = resolver.resolve(path);

            HttpResponse response = handler.handle(httpRequest);

            httpResponseWriter.write(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            try (OutputStream outputStream = connection.getOutputStream()) {
                HttpResponse response = errorMapper.toHttpResponse(e);
                httpResponseWriter.write(outputStream, response);
            } catch (IOException ioe) {
                log.error("Write failed: {}", ioe.getMessage());
            }
        }
    }
}
