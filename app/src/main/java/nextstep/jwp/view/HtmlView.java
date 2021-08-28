package nextstep.jwp.view;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.OutputStream;
import nextstep.jwp.http.response.HttpResponse;

public class HtmlView implements View {

    private static final String DELIMITER = "\r\n";
    private static final String COLON_AND_BLANK = ": ";
    private static final String BLANK = " ";
    private static final String FORMAT = "%s %s %s \r\n" +
                                         "%s\r\n" +
                                         "\r\n" +
                                         "%s";

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

        return String.format(FORMAT,
            httpResponse.protocolName(), httpResponse.statusCode(), httpResponse.statusName(),
            headers,
            httpResponse.responseBody().getBody()
        );
    }

    private String formatHeaderString() {
        return httpResponse.headers().map().entrySet().stream()
            .map(set -> set.getKey() + COLON_AND_BLANK + set.getValue().toValuesString() + BLANK)
            .collect(joining(DELIMITER));
    }
}
