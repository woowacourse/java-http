package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;

public record HttpResponse(String path, HttpStatus httpStatus){
}
