package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.mapper.Mapper;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
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
             final var outputStream = connection.getOutputStream()) {

            byte[] buffer = new byte[4096]; // TODO: 요청 준 것만큼 읽어야함
            int read = inputStream.read(buffer);
            HttpRequest request = new HttpRequest(new String(buffer));
            Map<String, String> params = request.getParams();
            log.info(params.toString());

            URL fileURL = Mapper.mapToFileURL(request.getPath());

            String responseBody = getFileContent(fileURL);
            HttpResponse response = new HttpResponse(responseBody);

            String extension = getFileExtension(fileURL);
            response.setContentType(extension);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFileContent(URL resourceURL) throws IOException {
        if (resourceURL != null) {
            File file = new File(resourceURL.getFile());
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String collect = reader.lines().collect(Collectors.joining("\n"));
                return collect + "\n";
            } catch (IOException e) {
                return "Hello world!";
            }
        }
        return "";
    }

    private String getFileExtension(URL resourceURL) {
        return resourceURL.getPath().substring(resourceURL.getPath().lastIndexOf('.') + 1);
    }

}
