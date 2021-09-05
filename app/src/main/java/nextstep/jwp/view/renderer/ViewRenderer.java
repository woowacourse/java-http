package nextstep.jwp.view.renderer;

import nextstep.jwp.handler.modelandview.Model;

public interface ViewRenderer {
    String render(Model model, String content);
}
