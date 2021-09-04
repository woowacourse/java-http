package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import nextstep.jwp.constants.Header;
import nextstep.jwp.constants.Http;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.controller.MappingHandler;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.PageNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.request.RequestHeader;
import nextstep.jwp.request.RequestLine;
import nextstep.jwp.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer {
    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

    private final HttpRequest httpRequest;
    private final MappingHandler mappingHandler;

    public HttpServer(InputStream inputStream) throws IOException {
        this.httpRequest = new HttpRequest(new BufferedReader(new InputStreamReader(inputStream)));
         this.mappingHandler = new MappingHandler(httpRequest);
    }

    public String getResponse() throws IOException {
        try {
            return mappingHandler.response();
        } catch (InvocationTargetException e) {
            log.info("Reflection related controller  Error {}", e.toString());
            return handleReflectionException(e);
        } catch (PageNotFoundException e) {
            log.info(e.getMessage());
            return HttpResponse
                    .statusCode(StatusCode.NOT_FOUND)
                    .responseResource("/404.html")
                    .build();
        } catch (UnauthorizedException e) {
            log.info(e.getMessage());
            return HttpResponse
                    .statusCode(StatusCode.UNAUTHORIZED)
                    .responseResource("/401.html")
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return HttpResponse
                    .statusCode(StatusCode.INTERNAL_SERVER_ERROR)
                    .responseResource("/500.html")
                    .build();
        }
    }

    private String handleReflectionException(InvocationTargetException e) throws IOException {
        if (e.getTargetException() instanceof BadRequestException) {
            return HttpResponse
                    .statusCode(StatusCode.BAD_REQUEST)
                    .responseResource("/400.html")
                    .build();
        }
        if (e.getTargetException() instanceof UnauthorizedException) {
            return HttpResponse
                    .statusCode(StatusCode.UNAUTHORIZED)
                    .responseResource("/401.html")
                    .build();
        }
        return HttpResponse
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR)
                .responseResource("/500.html")
                .build();
    }
}
