package org.apache.coyote.http11;

import com.techcourse.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import com.techcourse.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping();

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var inputStream = connection.getInputStream();
                var outputStream = connection.getOutputStream();
                var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest request = HttpRequestParser.parse(reader);
            HttpResponse response = new HttpResponse();

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);

            response.write(outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error("컨트롤러 실행 중 예외가 발생했습니다.", e);
        }
    }
}
