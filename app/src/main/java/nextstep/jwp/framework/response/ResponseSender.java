package nextstep.jwp.framework.response;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseSender {

    private final OutputStream outputStream;

    public ResponseSender(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static ResponseSender of(OutputStream outputStream) {
        return new ResponseSender(outputStream);
    }

    public void sendResponse(HttpResponse httpResponse) throws IOException {
        final String response = httpResponse.generateResponse();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
