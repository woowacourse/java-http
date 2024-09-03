package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.StaticResourceHandler;
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

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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

            // requestMapping -> 적절한 컨트롤러 호출
            StaticResourceHandler handler = new StaticResourceHandler();
            HttpResponse httpResponse = handler.handle(httpRequest);

            OutputView outputView = new OutputView(outputStream);
            outputView.write(HttpResponseDto.from(httpResponse));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
