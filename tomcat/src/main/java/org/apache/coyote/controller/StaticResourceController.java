package org.apache.coyote.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) {
        response.responseNotFound();
    }

    @Override
    protected void doGet(final Request request, final Response response) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String name = "static" + request.getRequestLine().getRequestPath();
        final URL fileURL = classLoader.getResource(name);

        if (fileURL == null) {
            response.responseNotFound();
            return;
        }

        final URI fileURI;
        try {
            fileURI = fileURL.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }

        final StringBuilder stringBuilder = new StringBuilder();
        try (final InputStream inputStream = new FileInputStream(Paths.get(fileURI).toFile());
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(nextLine)
                        .append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        response.writeBody(stringBuilder.toString());
    }
}
