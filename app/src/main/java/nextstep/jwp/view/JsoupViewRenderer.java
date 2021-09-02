package nextstep.jwp.view;

import java.util.Objects;
import nextstep.jwp.handler.modelandview.Model;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupViewRenderer implements ViewRenderer {
    public String render(Model model, String content) {
        Document parse = Jsoup.parse(content);
        for (String key : model.keys()) {
            Element element = parse.select("." + key).first();
            if(!Objects.isNull(element)){
                element.html(model.getAttribute(key).toString());
            }
        }
        return parse.html();
    }
}
