package org.apache.coyote.http11.handler.controllerResponse;

import org.apache.coyote.http11.httpResponse.HttpStatus;

public record JsonResponse(HttpStatus status, String content) implements ControllerResponse {

}
