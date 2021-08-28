package nextstep.jwp.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;
import nextstep.jwp.view.View;

public class HtmlView implements View {

    private static final String DELIMITER = "\r\n";
    private static final String COLON_AND_BLANK = ": ";

    private final HttpResponse httpResponse;

    public HtmlView(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {

        final String response = toResponseFormat();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String toResponseFormat() {
        final String headers = formatHeaderString();

        return String.join(DELIMITER,
            String.format("%s %s %s", httpResponse.protocol().getProtocolName(),
                httpResponse.status().getCode(), httpResponse.status().getMessage()),
            headers,
            "",
            httpResponse.responseBody().getBody());
    }

    private String formatHeaderString() {
        return httpResponse.headers().map().entrySet().stream()
            .map(set -> set.getKey() + COLON_AND_BLANK + set.getValue().toValuesString())
            .collect(Collectors.joining(DELIMITER));
    }
}
