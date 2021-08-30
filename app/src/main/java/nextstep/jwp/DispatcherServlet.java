package nextstep.jwp;

import nextstep.jwp.controller.FrontController;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class DispatcherServlet implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private final Socket connection;

    public DispatcherServlet(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(outputStream);

            FrontController frontController = new FrontController();
            frontController.service(httpRequest, httpResponse);

//            HandlerAdapter ha =  getHandlerAdapter(mappedHandler.getHandler());
////            Controller adapter = RequestMapping.getController(httpRequest.getRequestURI());
////            if (adapter == null) {
////                httpResponse.sendError("/401.html");
////                return;
////            }

//            ModelAndView mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
////            ModelAndView mv = adapter.handle(httpRequest, httpResponse);

//            processDispatchResult(httpRequest, httpResponse, handler)
////            processDispatchResult(httpRequest, httpResponse, mv);
//            FrontController frontController = new FrontController();
//            frontController.service(httpRequest, httpResponse);

            // 정적 컨텐츠 일때

            // 아예 없는 url 매핑 시
//            Controller controller = RequestMapping.getController(httpRequest.getRequestURI());
//            if (controller == null) {
//                if (FileUtils.existFile(httpRequest.getRequestURI())) {
//                    httpResponse.forward(httpRequest.getRequestURI());
//                    return;
//                }
//
//                httpResponse.sendError("/404.html");
//                return;
//            }
//
//            controller.service(httpRequest, httpResponse);
        } catch (IOException | ServletException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }
//    @Nullable
//	protected View resolveViewName(String viewName, @Nullable Map<String, Object> model,
//			Locale locale, HttpServletRequest request) throws Exception {
//
//		if (this.viewResolvers != null) {
//			for (ViewResolver viewResolver : this.viewResolvers) {
//				View view = viewResolver.resolveViewName(viewName, locale);
//				if (view != null) {
//					return view;
//				}
//			}
//		}
//		return null;
//	}

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
