package nextstep.jwp.web.network.request;

import nextstep.jwp.web.exception.InputException;
import nextstep.jwp.web.network.response.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;

public interface HttpBody {

    static HttpBody of(BufferedReader bufferedReader, HttpHeaders headers) {
        try {
            if (bufferedReader.ready()) {
                final int contentLength = headers.getContentLength();
                final char[] buffer = new char[contentLength];

                bufferedReader.read(buffer, 0, contentLength);
                return FormUrlEncodedHttpBody.of(new String(buffer));
            }
            return EmptyHttpBody.getInstance();
        } catch (IOException exception) {
            throw new InputException("body in http request");
        }
    }

    String getAttribute(String key);
}
