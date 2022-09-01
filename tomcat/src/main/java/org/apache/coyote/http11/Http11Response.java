package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public class Http11Response {

    private final OutputStream outputStream;

    public Http11Response(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(final HttP11StaticFile staticFile) throws IOException {
        final byte[] responseBodyBytes = getOkResponse(staticFile).getBytes();
        outputStream.write(responseBodyBytes);
        outputStream.flush();
    }

    private String getOkResponse(final HttP11StaticFile staticFile) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + staticFile.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + staticFile.getByteLength() + " ",
                "",
                staticFile.getContent());
    }
}
