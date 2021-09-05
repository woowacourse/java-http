package nextstep.jwp.view;

import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.view.renderer.JsoupHtmlRenderer;
import nextstep.jwp.view.renderer.ViewRenderer;

public class View {

    public static final View EMPTY = new View("", ContentType.empty());

    private final ViewRenderer htmlRenderer = new JsoupHtmlRenderer();
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
        Model model = modelAndView.getModel();
        String responseContent = parsing(model);

        if (!responseContent.isEmpty()) {
            httpResponse.setContent(responseContent, contentType.value());
        }
    }

    private String parsing(Model model) {
        if (contentType.equals(ContentType.HTML_UTF8) && !model.isEmpty()) {
            return htmlRenderer.render(model, content);
        }
        return content;
    }
}
