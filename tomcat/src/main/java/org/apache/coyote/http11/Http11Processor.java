package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.view.UserOutput;
import org.apache.coyote.Processor;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.FileExtension;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import org.utils.RequestLineParser;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final String requestStartLine = getRequestStartLine(inputStream);
            final Request request = new Request(requestStartLine);
            final String responseBody = getStaticResource(request.getPath());

            UserOutput.outputUserInformation(request);

            final MediaType mediaType = MediaType.of(FileExtension.of(requestStartLine));
            final String response = new Response.ResponseBuilder(HttpVersion.HTTP11, Status.OK)
                    .setContentType(mediaType, Charset.UTF8)
                    .setContentLength(responseBody.getBytes(StandardCharsets.UTF_8).length)
                    .setBody(responseBody)
                    .build()
                    .getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getStaticResource(final String requestUrl) throws IOException {
        final String resourcePath = RequestLineParser.getStaticResourcePath(requestUrl);

        final URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(resourcePath);
        assert url != null;
        return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }

    private String getRequestStartLine(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.readLine();
    }
}
