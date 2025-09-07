package org.apache.coyote.http11.handler.controllerResponse;

public record JsonResponse(String status, String content) implements ControllerResponse {

}
