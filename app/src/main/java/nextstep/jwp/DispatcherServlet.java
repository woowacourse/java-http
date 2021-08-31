package nextstep.jwp;

import com.sun.nio.sctp.IllegalUnbindException;
import nextstep.jwp.adapter.HandlerAdapter;
import nextstep.jwp.adapter.HandlerAdapters;
import nextstep.jwp.model.httpmessage.common.ContentType;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;
import nextstep.jwp.view.ModelAndView;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_LENGTH;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.*;


public class DispatcherServlet {

    public void service(HttpRequest request, HttpResponse response) throws IOException, ServletException {
        RequestMapping requestMapping = new RequestMapping();

        Object handler = requestMapping.getHandler(request);
        if (handler == null) {
            String path = request.getPath();
            ModelAndView mv = new ModelAndView();

            if (FileUtils.existFile(path)) { // 정적 파일인 경우
                response.setStatus(OK);
                ContentType contentType = ContentType.of(path)
                        .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 정적 파일입니다. (url : " + path + ")"));
                response.setContentType(contentType.value());
                View view = new View(FileUtils.getAbsolutePath(path));
                view.render(mv, response);
                return;
            }

            response.setStatus(NOT_FOUND);
            response.getHeaders().setContentType(HTML.value());

            mv.setViewName("/404.html");
            processDispatchResult(response, mv);
            return;
        }

        HandlerAdapters handlerAdapters = new HandlerAdapters();
        HandlerAdapter adapter = handlerAdapters.getHandlerAdapter(handler);
        if (adapter == null) {
            ModelAndView mv = new ModelAndView();
            response.setStatus(NOT_FOUND);
            response.getHeaders().setContentType(HTML.value());

            mv.setViewName("/404.html");
            processDispatchResult(response, mv);
            return;
        }

        ModelAndView mv = adapter.handle(request, response, handler);

        processDispatchResult(response, mv);
    }

    private void processDispatchResult(HttpResponse response, ModelAndView mv) throws IOException {
        View view;
        if (mv.getViewName() == null) { // view가 없는 경우`
            OutputStream outputStream = response.getOutputStream();
            if (mv.getModel().size() > 0) { // body가 있다면 stringmessageconverter?
                outputStream.write((response.getResponseLine().toString() + "\r\n").getBytes());
                outputStream.write(String.format("%s: %s \r\n", ContentType.CONTENT_TYPE_HEADER, response.getContentType()).getBytes());
                outputStream.write(String.format("%s: %s \r\n", CONTENT_LENGTH.value(), response.getContentLength()).getBytes());
                outputStream.write("\r\n".getBytes());
                outputStream.write(((String) mv.getModel().get("body")).getBytes());
                outputStream.flush();
                return;
            }
            throw new IllegalArgumentException("출력할 수 없는 결과입니다.");
        }

        String viewName = mv.getViewName();
        if (viewName != null) {
            ViewResolver viewResolver = new ViewResolver();
            view = viewResolver.resolveViewName(viewName);
            if (view == null) {
                throw new IllegalUnbindException("cannot bind view. (viewName : " + viewName + ")");
            }

            if (response.getStatus() == REDIRECT) {
                view.redirect(mv, response);
                return;
            }

            view.render(mv, response);
        }
    }
}
