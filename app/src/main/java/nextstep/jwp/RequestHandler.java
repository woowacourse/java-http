package nextstep.jwp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.dispatcher.adapter.HandlerAdapter;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpResponseImpl;
import nextstep.jwp.http.message.ContentType;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpStatus;
import nextstep.jwp.http.parser.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    private final ApplicationContext applicationContext;

    private final List<HandlerMapping> handlerMappings;

    private final List<HandlerAdapter> handlerAdapters;

    public RequestHandler(Socket connection, ApplicationContext applicationContext,
        List<HandlerMapping> handlerMappings,
        List<HandlerAdapter> handlerAdapters) {
        this.connection = connection;
        this.applicationContext = applicationContext;
        this.handlerMappings = handlerMappings;
        this.handlerAdapters = handlerAdapters;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        HttpRequest request;
        HttpResponse response = new HttpResponseImpl();
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            request = HttpParser.parse(inputStream);
            request.setApplicationContext(applicationContext);

            if (log.isDebugEnabled()) {
                log.debug("\r\n============== request ==============\r\n{}", request.asString());
            }

            HandlerMapping mappedHandler = getHandler(request);

            HandlerAdapter handlerAdapter = getHandlerAdapter(mappedHandler);

            handlerAdapter.handle(request, response, mappedHandler.getHandler(request));

            if (log.isDebugEnabled()) {
                log.debug("\r\n============== response ==============\r\n{}", response.asString());
            }

            outputStream.write(response.asString().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception e) {
            e.printStackTrace();
            renderInternalServerError(response);
        } finally {
            close();
        }
    }

    private HandlerMapping getHandler(HttpRequest request) {
        return this.handlerMappings
            .stream()
            .filter(handlerMapping -> handlerMapping.supports(request))
            .findFirst()
            .orElseThrow();
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping mappedHandler) {
        return this.handlerAdapters
            .stream()
            .filter(handlerAdapter -> handlerAdapter.supports(mappedHandler))
            .findFirst()
            .orElseThrow();
    }

    private void renderInternalServerError(HttpResponse response) {
        URL fileResource = getClass().getClassLoader().getResource("./static/500.html");
        Path path = Paths.get(fileResource.getPath());
        try (InputStream fileInputStream = new FileInputStream(path.toFile())) {
            byte[] bytes = fileInputStream.readAllBytes();
            String content = new String(bytes);

            response.setContent(content);
        } catch (IOException e) {
            throw new NotFoundException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getDescription());
        headers.addHeader("Content-Length", String.valueOf(response.getContentAsString().length()));

        response.setHeaders(headers);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setVersionOfProtocol("HTTP/1.1");
    }

    private void close() {
        try {

            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
