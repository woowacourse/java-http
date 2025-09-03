package org.apache.coyote.request.requestInfo;


import java.util.List;

public class RequestInfo {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;
    
    private static final int REQUEST_INFO_SIZE = 3;
    private static final String REQUEST_INFO_SEPARATOR = " ";

    private final RequestMethod requestMethod;
    private final String requestPath; //todo: requestPath, protocolVersion 객체 만들기
    private final String protocolVersion;

    public RequestInfo(final String requestInfo) {
        final List<String> requestInfos = List.of(requestInfo.split(REQUEST_INFO_SEPARATOR));
        validateRequestInfos(requestInfos);

        this.requestMethod = RequestMethod.from(requestInfos.get(REQUEST_METHOD_INDEX));
        this.requestPath = requestInfos.get(REQUEST_PATH_INDEX);
        this.protocolVersion = requestInfos.get(PROTOCOL_VERSION_INDEX);
    }

    private void validateRequestInfos(final List<String> requestInfos) {
        if(requestInfos.size() != REQUEST_INFO_SIZE){
            throw new IllegalArgumentException("[ERROR] 요청 형식이 올바르지 않습니다");
        }
    }

    public boolean isSame(final RequestMethod requestMethod) {
        return this.requestMethod.equals(requestMethod);
    }

    public boolean isDefaultPath() {
        return this.requestPath.equals("/");
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getRequestPathExtension() {
        if (!requestPath.contains(".")) {
            throw new IllegalArgumentException("확장자를 찾을 수 없습니다.");
        }
        return requestPath.split("\\.")[1];
    }

    public boolean isStartsWith(final String loginPath) {
        return requestPath.startsWith(loginPath);
    }
}
