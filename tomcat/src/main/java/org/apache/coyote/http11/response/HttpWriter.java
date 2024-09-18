package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class HttpWriter extends OutputStreamWriter {


    public HttpWriter(OutputStream out) {
        super(out);
    }

    public void flushWith(String data) throws IOException {
        super.write(data);
        flush();
    }
}
