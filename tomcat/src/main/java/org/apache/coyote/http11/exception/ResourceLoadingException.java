package org.apache.coyote.http11.exception;

public class ResourceLoadingException extends RuntimeException {

    public ResourceLoadingException(final String resourceName) {
        super("리소스를 읽어오는 과정에서 오류가 발생하였습니다. resource = " + resourceName);
    }
}
