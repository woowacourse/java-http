package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.controller.Controller;
import org.apache.coyote.http11.domain.controller.RequestMapping;
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

    public Http11Processor(
            Socket connection,
            RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.requestMapping = requestMapping;
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
            HttpRequest httpRequest = readHttpRequest(inputView);

            Controller controller = requestMapping.getController(httpRequest);
            HttpResponse httpResponse = controller.service(httpRequest);

            OutputView outputView = new OutputView(outputStream);
            outputView.write(HttpResponseDto.from(httpResponse));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(InputView inputView) throws IOException {
        String requestLine = inputView.readLine();
        readHttpHeaders(inputView);
        List<String> headerLines = List.of();
        return new HttpRequest(requestLine, headerLines);
    }

    private List<String> readHttpHeaders(InputView inputView) throws IOException {
        ArrayList<String> headerLines = new ArrayList<>();
        String line = inputView.readLine();
        while (!line.isEmpty()) {
            headerLines.add(line);
            line = inputView.readLine();
        }
        
        return headerLines;
    }

}
