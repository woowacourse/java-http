package nextstep.jwp.view;

import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;

public class View {

    private static final View EMPTY = new View("", ContentType.empty());

    private final String content;
    private final ContentType contentType;

    private View(String content, ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static View of(String viewName, String content) {
        return new View(content, ContentType.fromFileName(viewName));
    }

    public void render(ModelAndView modelAndView, HttpResponse httpResponse) {
        String rendered = renderedView(modelAndView.getModel());
        if (!rendered.isEmpty()) {
            httpResponse.addHeader("Content-Type", contentType());
            httpResponse.addHeader("Content-Length", String.valueOf(contentLength()));
            httpResponse.setContent(rendered, contentType.value());
        }
    }

    private String renderedView(Model model) {
        String rendered = content;
        for (String key : model.keys()) {
            Object attribute = model.getAttribute(key);
            rendered = rendered.replace("${"+key+"}", attribute.toString());
        }
        return rendered;
    }

    public static View empty() {
        return EMPTY;
    }

    public String contentType() {
        return contentType.value();
    }

    public int contentLength() {
        return content.getBytes().length;
    }

    public String content() {
        return content;
    }
}
