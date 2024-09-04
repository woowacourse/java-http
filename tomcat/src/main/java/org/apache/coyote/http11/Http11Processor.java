package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.controller.Controller;
import org.apache.coyote.http11.domain.controller.RequestMapping;
import org.apache.coyote.http11.domain.controller.StaticResourceHandler;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.dto.HttpResponseDto;
import org.apache.coyote.http11.view.InputView;
import org.apache.coyote.http11.view.OutputView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final StaticResourceHandler staticResourceHandler;

    public Http11Processor(
            Socket connection,
            RequestMapping requestMapping,
            StaticResourceHandler staticResourceHandler
    ) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.staticResourceHandler = staticResourceHandler;
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
            InputView inputView = new InputView(new BufferedReader(new InputStreamReader(inputStream)));
            HttpRequest httpRequest = new HttpRequest(inputView.readLine());

            HttpResponse httpResponse = handle(httpRequest);

            OutputView outputView = new OutputView(outputStream);
            outputView.write(HttpResponseDto.from(httpResponse));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(HttpRequest httpRequest) throws IOException {
        if (requestMapping.canHandle(httpRequest)) {
            Controller controller = requestMapping.getController(httpRequest);
            return controller.service(httpRequest);
        }
        return staticResourceHandler.handle(httpRequest);
    }
}
