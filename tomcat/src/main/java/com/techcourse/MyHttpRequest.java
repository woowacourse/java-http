package com.techcourse;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class MyHttpRequest {
    private final RequestLine requestLine;
    private final MyHeaders header;
    private final MyPayload payload;

    public MyHttpRequest(InputStream inputStream) {
        List<String> lines = StreamToStringConverter.convertToLines(inputStream);
        this.requestLine = RequestLine.from(lines.getFirst());
        this.header = MyHeaders.from(lines.subList(1, lines.size()-1));
        this.payload = getPayload(lines.getLast());
    }

    private MyPayload getPayload(String payload) {
        if (requestLine.getMethod() != HttpProtocol.POST) {
            return null;
        }
        return MyPayload.from(payload);
    }

    public String getMethod() {
        return requestLine.getMethod().name();
    }

    public String getLocation() {
        return requestLine.getLocation();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String key) {
        return header.find(key);
    }
    public boolean containsHeader(String key){
        return header.contains(key);
    }

    public void setHeader(String key, String value) {
        header.set(key, value);
    }


    public Map<String, String> getPayload(){
        return payload.getValue();
    }

}
