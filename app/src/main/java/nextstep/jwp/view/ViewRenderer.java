package nextstep.jwp.view;

import nextstep.jwp.handler.modelandview.Model;

public interface ViewRenderer {
    String render(Model model, String content);
}
