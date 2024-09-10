package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.ServletContainer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = convertRequest(inputStream);
            HttpResponse response = new ServletContainer().dispatch(request);

            outputStream.write(response.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest convertRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> header = new ArrayList<>();
        int contentLength = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith(HttpHeaderName.CONTENT_LENGTH.getHeaderName())) {
                contentLength = Integer.parseInt(line.split(HttpHeader.HEADER_DELIMITER)[1].trim());
            }
            header.add(line);
        }

        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);

        return HttpRequest.of(header, new String(body));
    }
}
