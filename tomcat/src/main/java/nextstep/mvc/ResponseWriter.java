package nextstep.mvc;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class ResponseWriter {

    private ResponseWriter() {
    }

    public static void view(HttpResponse httpResponse, HttpStatus httpStatus, String path) {
        httpResponse.setHttpStatus(httpStatus);
        ModelAndView modelAndView = new ModelAndView(path);
        resolveAndRenderView(modelAndView, httpResponse);
    }

    private static void resolveAndRenderView(ModelAndView modelAndView, HttpResponse httpResponse) {
        View view = new View();
        if (modelAndView.getViewName() != null) {
            view = ViewResolver.resolve(modelAndView.getViewName());
        }
        view.render(modelAndView.getModel(), httpResponse);
    }

    public static void writeWithData(HttpResponse httpResponse, HttpStatus httpStatus, String data) {
        httpResponse.setHttpStatus(httpStatus);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setAttribute("data", data);
        resolveAndRenderView(modelAndView, httpResponse);
    }

    public static void redirect(final HttpResponse httpResponse,
            final String path) {
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.addHeader(HttpHeaders.LOCATION, path);
    }
}
