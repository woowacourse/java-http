package org.apache.coyote.http11;

import com.techcourse.controller.GreetingController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HttpResourceController;
import org.apache.coyote.http11.error.ErrorMapper;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final QueryParser queryParser;
    private final HttpRequestReader httpRequestReader;
    private final HttpResourceLoader httpResourceLoader;
    private final HttpResourceController httpResourceController;
    private final HttpResponseWriter httpResponseWriter;
    private final SessionManager sessionManager;
    private final Resolver resolver;
    private final ErrorMapper errorMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.queryParser = new QueryParser();
        this.httpRequestReader = new HttpRequestReader(queryParser);
        this.httpResourceLoader = new HttpResourceLoader();
        this.httpResourceController = new HttpResourceController(httpResourceLoader);
        this.httpResponseWriter = new HttpResponseWriter();
        this.sessionManager = new SessionManager();
        this.resolver = new Resolver(httpResourceController)
                .register("/", new GreetingController())
                .register("/login", new LoginController(httpResourceLoader, queryParser, sessionManager))
                .register("/register", new RegisterController(httpResourceLoader, queryParser))
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
            String path = httpRequest.getPath();
            Controller controller = resolver.resolve(path);
            HttpResponse httpResponse = HttpResponse.create();
            controller.service(httpRequest, httpResponse);
            httpResponseWriter.write(outputStream, httpResponse);
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
