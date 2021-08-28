package nextstep.jwp.framework.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import nextstep.jwp.framework.http.formatter.LineFormatter;
import nextstep.jwp.framework.http.formatter.StatusLineFormatter;

public class HttpResponseWriter {

    private LineFormatter lineFormatter;

    public HttpResponseWriter(HttpResponse httpResponse) {
        this.lineFormatter = new StatusLineFormatter(httpResponse);
    }

    public void writeWith(OutputStream outputStream) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(readFromResponse());
        writer.flush();
    }

    public String readFromResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        while (lineFormatter.canRead()) {
            stringBuilder.append(lineFormatter.transform());
            lineFormatter = lineFormatter.convertNextFormatter();
        }
        return stringBuilder.toString();
    }
}
