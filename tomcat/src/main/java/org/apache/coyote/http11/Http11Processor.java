package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.handler.HttpRequestHandlerContainer;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final HttpRequestHandlerContainer handlerContainer = new HttpRequestHandlerContainer();

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final String request = parseRequest(inputStream);
            final HttpResponse response = processResponse(request);

            outputStream.write(response.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String parseRequest(InputStream inputStream) throws IOException {
        StringBuilder headerBuilder = new StringBuilder();
        int contentLength = 0;
        int lastByte = -1;
        int secondLastByte = -1;
        int currentByte;

        while ((currentByte = inputStream.read()) != -1) {
            headerBuilder.append((char) currentByte);
            if (currentByte == 10 && lastByte == 13 && secondLastByte == 10) {
                String header = headerBuilder.toString();
                String[] lines = header.split("\r\n");
                for (String line : lines) {
                    if (line.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
                        break;
                    }
                }
                break;
            }
            secondLastByte = lastByte;
            lastByte = currentByte;
        }

        if (contentLength > 0) {
            byte[] bodyBytes = new byte[contentLength];
            int totalBytesRead = 0;
            int bytesRead;

            while (totalBytesRead < contentLength
                    && (bytesRead = inputStream.read(bodyBytes, totalBytesRead, contentLength - totalBytesRead))
                    != -1) {
                totalBytesRead += bytesRead;
            }
            String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
            headerBuilder.append(bodyString);
        }

        return headerBuilder.toString();
    }

    private HttpResponse processResponse(String request) {
        String url = getUrl(request);
        HttpRequestHandler httpRequestHandler = handlerContainer.getHandler(url);
        if (httpRequestHandler == null) {
            throw new IllegalArgumentException("No resource found: " + url);
        }
        return httpRequestHandler.handle(request);
    }

    private String getUrl(String request) {
        return request.split("\r\n")[0].split(" ")[1].split("\\?")[0];
    }
}
