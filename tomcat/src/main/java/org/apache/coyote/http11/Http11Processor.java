package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.converter.MessageConverter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.parser.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            HttpRequest request = HttpRequestParser.parse(bufferedReader);
            HttpResponse response = new HttpResponse(request.getHttpVersion());
            delegateDataProcess(request, response);
            String httpResponseMessage = MessageConverter.convertHttpResponseToMessage(response);
            bufferedWriter.write(httpResponseMessage);
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void delegateDataProcess(HttpRequest request, HttpResponse response) {
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.getController(request)
                .ifPresent(controller -> controller.service(request, response));
    }
}
