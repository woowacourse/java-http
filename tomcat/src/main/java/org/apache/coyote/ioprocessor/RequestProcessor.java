package org.apache.coyote.ioprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.apache.coyote.ioprocessor.parser.RequestReader;

public class RequestProcessor {
    private final RequestReader reader;

    public RequestProcessor(InputStream inputStream) throws IOException, URISyntaxException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestUri = reader.readLine();
        this.reader = new RequestReader(requestUri);
    }

    public String processRequestBody() throws IOException, URISyntaxException {
        if (reader.isRootUri()) {
            return "Hello world!";
        }
        return reader.readResource();
    }
}
