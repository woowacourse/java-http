package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            InputView inputView = new InputView(new BufferedReader(inputStreamReader));
            HttpRequest httpRequest = readHttpRequest(inputView);

            Controller controller = requestMapping.getController(httpRequest);
            HttpResponse httpResponse = controller.service(httpRequest);

            OutputView outputView = new OutputView(outputStreamWriter);
            outputView.write(HttpResponseDto.from(httpResponse));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(InputView inputView) throws IOException {
        String requestLine = inputView.readLine();
        List<String> headerLines = readHttpHeaders(inputView);
        String requestBody = readRequestMessage(inputView);

        return new HttpRequest(requestLine, headerLines, requestBody);
    }

    private List<String> readHttpHeaders(InputView inputView) throws IOException {
        ArrayList<String> headerLines = new ArrayList<>();
        while (inputView.isReadable()) {
            headerLines.add(inputView.readLine());
        }

        return headerLines;
    }

    private String readRequestMessage(InputView inputView) throws IOException {
        if (!inputView.isReadable()) {
            return StringUtils.EMPTY;
        }

        return inputView.readLine();
    }
}
