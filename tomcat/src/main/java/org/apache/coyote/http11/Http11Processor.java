package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
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
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            String resourceName = getResourceName(inputStream);
            String responseBody = parseStartLine(resourceName);
            String resourceExtension = getExtension(resourceName);

            HttpResponse response = new HttpResponse(responseBody, resourceExtension);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getExtension(String resourceName) {
        int extensionIndex = resourceName.indexOf('.') + 1;
        if (extensionIndex == 0) {
            return "html";
        }
        return resourceName.substring(extensionIndex);
    }

    private String getResourceName(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String startLine = bufferedReader.readLine();
        return startLine.split(" ")[1];
    }

    private String parseStartLine(String resourceName) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader()
                .getResource("static" + resourceName);

        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }
}
