package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.exception.ExceptionHandler;
import nextstep.jwp.exception.HttpGlobalException;
import nextstep.jwp.exception.InvalidRequestMethodException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    protected AbstractController() {
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            if (request.isGetMethod()) {
                doGet(request, response);

                return;
            }

            if (request.isPostMethod()) {
                doPost(request, response);

                return;
            }

            throw new InvalidRequestMethodException("지원하지 않는 메서드입니다."
                    + " 요청 URL : " + request.getNativePath()
                    + " Method : " + request.getHttpMethod());
        } catch (HttpGlobalException e) {
            ExceptionHandler.handle(response, e.getHttpStatus());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            ExceptionHandler.handle(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;

    public abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;
}
