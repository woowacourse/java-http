package nextstep.jwp.controller;

import nextstep.jwp.RequestMapping;
import nextstep.jwp.adapter.HandlerAdapter;
import nextstep.jwp.adapter.HandlerAdapters;
import nextstep.jwp.model.httpmessage.common.ContentType;
import nextstep.jwp.util.FileUtils;
import nextstep.jwp.view.ModelAndView;
import nextstep.jwp.view.View;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.model.httpmessage.response.HttpStatus;
import nextstep.jwp.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;

import static nextstep.jwp.model.httpmessage.response.HttpStatus.NOT_FOUND;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.UNAUTHORIZED;
import static nextstep.jwp.model.httpmessage.response.ResponseLine.PROTOCOL;


public class FrontController {

    private static final Logger LOG = LoggerFactory.getLogger(FrontController.class);

    public void service(HttpRequest request, HttpResponse response) throws IOException, ServletException {
//        String requestURI = request.getRequestURI();
//        LOG.debug("Request URI : {}", requestURI);
//
//        Controller controller = RequestMapping.getController(requestURI);
//        if (controller == null) {
//            response.setStatus(NOT_FOUND);
//            response.sendError("/404.html");
//            return;
//        }
//
//        controller.process(request, response);
        RequestMapping requestMapping = new RequestMapping();
        Object handler = requestMapping.getHandler(request);
        if (handler == null) { // url 매핑이 없는 경우
            String path = request.getPath();
            if (FileUtils.existFile(path)) { // 정적 파일이 있는 경우
                response.forward(request.getPath());
                return;
            }
//            response.setStatus(HttpStatus.NOT_FOUND);
            response.sendError(NOT_FOUND);
            return;
        }

        HandlerAdapters handlerAdapters = new HandlerAdapters();
        HandlerAdapter adapter = handlerAdapters.getHandlerAdapter(handler);
        if (adapter == null) {
            response.sendError(NOT_FOUND);
            return;
        }

        ModelAndView mv = adapter.handle(request, response, handler);

//        processDispatchResult(request, response, mv);
    }

    private void processDispatchResult(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        // render()
        View view;
        if (mv.getViewName() == null) {
            OutputStream outputStream = response.getOutputStream();
            if (mv.getModel().size() > 0) {
                outputStream.write(String.format("%s %s ", PROTOCOL, mv.getStatus()).getBytes());
                outputStream.write(String.format("%s: %s ", ContentType.STRING, response.getHeader(ContentType.STRING)).getBytes());
                outputStream.write(((String) mv.getModel().get("body")).getBytes());
                outputStream.flush();
                return;
            }
            throw new IllegalArgumentException("출력할 수 없는 결과입니다.");
        }

        /*
         View가 있는 경우 (index.html, login.html ..)
         */
        String viewName = mv.getViewName();
        if (viewName != null) {
            ViewResolver viewResolver = new ViewResolver();
            view = viewResolver.resolveViewName(viewName);
            if (view == null) {
                throw new IllegalStateException("cannot found view");
            }
        }

        view = new View(viewName);
        if (mv.getStatus() != null) {
            response.setStatus(mv.getStatus());
        }
        view.render(mv.getModel(), response);
    }
}
