package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestAssembler;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.controller.RequestMapping;

public class Http11Processor implements Runnable, Processor {

    private final Socket connection;
    private final HttpRequestAssembler requestAssembler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        requestAssembler = new HttpRequestAssembler();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            HttpRequest request = requestAssembler.makeRequest(bufferedReader);
            execute(outputStream, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(OutputStream outputStream, HttpRequest request) throws Exception {
        HttpResponse response = new HttpResponse();
        RequestMapping.mapping(request, response);

        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }
}
