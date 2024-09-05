package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.handler.ResourceRequestHandler;
import org.apache.coyote.handler.RootRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<RequestHandler> requestHandlers;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestHandlers = findImplementations();
    }

    private List<RequestHandler> findImplementations() {
        return List.of(
                new RootRequestHandler(),
                new ResourceRequestHandler()
        );
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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final List<String> lines = getLines(bufferedReader);
            final Http11Request request = new Http11Request(lines);
            String contentType = "";
            String responseBody = "";
            for (RequestHandler requestHandler : requestHandlers) {
                if (requestHandler.canHandling(request)) {
                    contentType = requestHandler.getContentType(request);
                    responseBody = requestHandler.getResponseBody(request);
                    break;
                }
            }
            if (request.existsQueryParam()) {
                Map<String, String> queryParam = request.getQueryParam();
                Optional<User> user = InMemoryUserRepository.findByAccount(queryParam.get("account"));
                log.info(user.get().toString());
            }
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }

    }

    private List<String> getLines(BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }
}
