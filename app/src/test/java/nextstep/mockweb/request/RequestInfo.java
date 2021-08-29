package nextstep.mockweb.request;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nextstep.jwp.webserver.request.HttpMethod;

public class RequestInfo {

    static class FormData {

        private String key;
        private String value;
        public FormData(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String formDataForm() {
            return String.format("%s=%s", key, value);
        }

    }
    private static final String ENTER = "\r\n";

    private HttpMethod httpMethod;

    private String url;
    private Object body;
    private HeaderInfo headerInfo;
    private List<FormData> formData;
    public RequestInfo(HttpMethod httpMethod, String url, Object body) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.body = body;
        this.headerInfo = new HeaderInfo();
        this.formData = new ArrayList<>();
    }

    public String asRequest() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeRequestLine());
        stringBuilder.append(makeRequestHeader());
        stringBuilder.append(ENTER);
        stringBuilder.append(makeRequestBody());
        return stringBuilder.toString();
    }

    private String makeRequestLine() {
        final String requestLine = String.format("%s %s HTTP/1.1 %s", httpMethod.name(), url, ENTER);
        return requestLine;
    }

    private String makeRequestHeader() {
        if(!formData.isEmpty()) {
            final String formData = this.formData.stream().map(FormData::formDataForm)
                    .collect(joining("&"));
            headerInfo.addHeader("Content-Length", String.valueOf(formData.getBytes().length));
            body = formData;
        }
        return headerInfo.asRequestForm();
    }

    private String makeRequestBody() {
        if(body == null) {
            return "";
        }
        // TODO : Converter 기능 추가
        return (String) body;
    }

    public void addHeader(Map<String, String> headers) {
        headers.forEach((key,value) -> headerInfo.addHeader(key, value));
    }

    public void addFormData(String key, String value) {
        formData.add(new FormData(key, value));
    }

    public void addHeader(String key, String value) {
        headerInfo.addHeader(key, value);
    }
}
