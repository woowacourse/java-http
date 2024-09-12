package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.response.HttpResponse;

public class OutputProcessor {


    public void process(OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        String response = httpResponse.toHttpForm();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
