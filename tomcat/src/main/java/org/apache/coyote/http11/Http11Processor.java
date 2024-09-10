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
import org.apache.coyote.http11.domain.request.RequestBody;
import org.apache.coyote.http11.domain.request.RequestHeaders;
import org.apache.coyote.http11.domain.request.RequestLine;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;
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
            HttpResponse httpResponse = HttpResponse.status(HttpStatus.OK).build();

            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            OutputView outputView = new OutputView(outputStreamWriter);
            outputView.write(HttpResponseDto.from(httpResponse));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(InputView inputView) throws IOException {
        RequestLine requestLine = new RequestLine(inputView.readLine());
        RequestHeaders requestHeaders = new RequestHeaders(readHttpHeaders(inputView));
        RequestBody requestBody = new RequestBody(readRequestBody(inputView, requestHeaders));
        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private List<String> readHttpHeaders(InputView inputView) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while (!StringUtils.isEmpty(line = inputView.readLine())) {
            headerLines.add(line);
        }

        return headerLines;
    }

    private String readRequestBody(InputView inputView, RequestHeaders requestHeaders) throws IOException {
        String contentLengthHeader = requestHeaders.getHeader("Content-Length");
        if (contentLengthHeader == null) {
            return StringUtils.EMPTY;
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        return inputView.read(contentLength);
    }
}
