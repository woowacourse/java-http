package nextstep.mvc;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpResponse;

public class View {

    private static final String DEFAULT_CHAR_SET = "text/html;charset=utf-8";

    private String content;
    private final String contentType;

    public View() {
        this.contentType = DEFAULT_CHAR_SET;
    }

    public View(final String content, final String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public void render(Map<String, String> model, HttpResponse httpResponse) {
        if (content == null) {
            StringBuilder sb = new StringBuilder();
            for (final var value : model.values()) {
                sb.append(value);
            }
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_CHAR_SET);
            httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH,
                    String.valueOf(sb.toString().getBytes().length));
            httpResponse.setBody(sb.toString());
            return;
        }

        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH,
                String.valueOf(content.getBytes().length));
        httpResponse.setBody(content);
    }
}
