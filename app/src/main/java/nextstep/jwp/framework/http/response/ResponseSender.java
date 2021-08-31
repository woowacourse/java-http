package nextstep.jwp.framework.http.response;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseSender {

    private final OutputStream outputStream;

    public ResponseSender(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static ResponseSender of(final OutputStream outputStream) {
        return new ResponseSender(outputStream);
    }

    public void sendResponse(final HttpResponse httpResponse) throws IOException {
        final String response = httpResponse.generateResponse();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
