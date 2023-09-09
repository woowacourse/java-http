package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.NotCorrectPasswordException;
import org.apache.coyote.http11.exception.NotFoundAccountException;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.response.ResponsePage.INTERNAL_SERVER_ERROR_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.NOT_FOUND_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.UNAUTHORIZED_PAGE;

public class AbstractController implements Controller {
    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            process(request, response);
        } catch (NotFoundAccountException | NotCorrectPasswordException e) {
            response.foundResponse(UNAUTHORIZED_PAGE.gerResource());
        } catch (ResourceNotFoundException | UnsupportedOperationException e) {
            response.foundResponse(NOT_FOUND_PAGE.gerResource());
        } catch (Exception e) {
            response.foundResponse(INTERNAL_SERVER_ERROR_PAGE.gerResource());
        }
    }

    private void process(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod httpMethod = request.getMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
