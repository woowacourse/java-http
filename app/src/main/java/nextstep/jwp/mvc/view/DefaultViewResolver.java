package nextstep.jwp.mvc.view;

import static nextstep.jwp.webserver.response.ContentType.HTML;

import nextstep.jwp.mvc.exception.PageNotFoundException;
import nextstep.jwp.webserver.exception.NoFileExistsException;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class DefaultViewResolver implements ViewResolver {

    private static final String REDIRECT_FORM = "redirect:";

    @Override
    public void render(ModelAndView modelAndView, HttpRequest httpRequest,
            HttpResponse httpResponse) {
        final String viewName = modelAndView.getViewName();
        if(viewName.startsWith(REDIRECT_FORM)) {
            final String redirectUrl = viewName.substring(REDIRECT_FORM.length());
            httpResponse.flushAsRedirect(redirectUrl);
            return;
        }
        try {
            httpResponse.addPage("static/" + viewName);
            httpResponse.addHeader("Content-Type", HTML.contentType());
            httpResponse.flush();
        } catch (NoFileExistsException noFileExistsException) {
            throw new PageNotFoundException();
        }
    }
}
