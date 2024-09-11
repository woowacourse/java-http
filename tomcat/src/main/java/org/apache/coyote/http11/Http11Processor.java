package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.file.FileFinder;
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
            HttpResponse response = new HttpResponse(request);
            delegateDataProcess(request, response);
            addNoRedirect(response, request);
            bufferedWriter.write(response.convertToMessage());
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void delegateDataProcess(HttpRequest request, HttpResponse response) throws Exception {
        RequestMapping requestMapping = new RequestMapping();
        Optional<Controller> optionalController = requestMapping.getController(request);
        if (optionalController.isPresent()) {
            Controller controller = optionalController.get();
            controller.service(request, response);
        }
    }

    private void addNoRedirect(HttpResponse response, HttpRequest request) throws IOException {
        if (response.notHasLocation()) {
            FileFinder fileFinder = new FileFinder(request);
            fileFinder.resolve(response);
        }
    }
}
