package org.apache.coyote.http11.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.ViewController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class ApiHandler extends Handler{
    private static final List<API> apiList = new ArrayList<>();
    private Handler next;

    static{
        addApi("/", ViewController::getVoid,"Hello world!",false,false);
        addApi("/login", ViewController::getLogin,"login.html",false,false);
        addApi("/register", ViewController::getRegister,"register.html",false,false);
        addApi("/login", LoginController::login,"index.html",false,true);
        addApi("/register", LoginController::signUp,"index.html",false, true);
    }

    @Override
    void setNext(Handler handler) {
        this.next = handler;
    }

    @Override
    String getResponse(Request request) {
        for(API api : apiList){
            boolean isHandle = api.isHandle(request);
            if(isHandle){
                Response response = api.controller.apply(request);
                return response.getResponse();
            }
        }
        return next.getResponse(request);
    }

    public static void addApi(
            String api,
            Function<Request, Response> controller,
            String file,
            boolean isQueryHandle,
            boolean isBodyHandle
    ){
        apiList.add(new API(api,controller,file,isQueryHandle,isBodyHandle));
    }

    private static class API{
        private final String uri;
        private final Function<Request, Response> controller;
        private final String file;
        private final boolean handleQuery;
        private final boolean handleBody;

        public API(String uri, Function<Request, Response> controller, String file, boolean handleQuery,
                   boolean handleBody) {
            this.uri = uri;
            this.controller = controller;
            this.file = file;
            this.handleQuery = handleQuery;
            this.handleBody = handleBody;
        }

        private boolean isHandle(Request request){
            String api = request.getUri().split("\\?")[0];
            boolean isQuery = !request.getQuery().isEmpty();
            boolean isBody = !request.getBody().isEmpty();
            return this.uri.equals(api) && handleBody == isBody && handleQuery == isQuery;
        }
    }
}
