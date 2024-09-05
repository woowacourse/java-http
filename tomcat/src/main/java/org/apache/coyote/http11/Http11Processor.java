package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import org.apache.Serializer;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.handler.LoginRequestHandler;
import org.apache.coyote.handler.NotFoundHandler;
import org.apache.coyote.handler.RootRequestHandler;
import org.apache.coyote.handler.SignupRequestHandler;
import org.apache.coyote.handler.StaticResourceRequestHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.request.Http11RequestHeaders;
import org.apache.coyote.http11.request.Http11RequestLine;
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
                new LoginRequestHandler(),
                new SignupRequestHandler(),
                new StaticResourceRequestHandler()
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

            final Http11Request request = readRequest(bufferedReader);
            boolean handled = false;
            for (RequestHandler requestHandler : requestHandlers) {
                if (requestHandler.canHandling(request)) {
                    HttpResponse response = requestHandler.handle(request);
                    outputStream.write(Serializer.serialize(response));
                    outputStream.flush();
                    handled = true;
                    break;
                }
            }
            if (!handled) {
                HttpResponse response = new NotFoundHandler().handle(request);
                outputStream.write(Serializer.serialize(response));
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }

    }

    private Http11Request readRequest(BufferedReader bufferedReader) throws IOException {
        final Http11RequestLine requestLine = getLine(bufferedReader);
        final Http11RequestHeaders requestHeaders = getHeaders(bufferedReader);
        final Http11RequestBody requestBody = getBody(bufferedReader, requestHeaders.getValue("Content-Length"));
        final Http11Request request = new Http11Request(requestLine, requestHeaders, requestBody);
        return request;
    }

    private Http11RequestLine getLine(BufferedReader bufferedReader) throws IOException {
        return new Http11RequestLine(bufferedReader.readLine());
    }

    private Http11RequestHeaders getHeaders(BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return new Http11RequestHeaders(lines);
    }

    private Http11RequestBody getBody(BufferedReader bufferedReader, String contentLength) throws IOException {
        try {
            int bodyLength = Integer.parseInt(contentLength);
            char[] buffer = new char[bodyLength];
            bufferedReader.read(buffer, 0, bodyLength);
            return new Http11RequestBody(new String(buffer));
        } catch (NumberFormatException e) {
            return new Http11RequestBody("");
        }
    }
}
