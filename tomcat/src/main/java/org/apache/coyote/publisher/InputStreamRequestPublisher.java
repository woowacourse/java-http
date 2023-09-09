package org.apache.coyote.publisher;

import org.apache.coyote.common.Headers;
import org.apache.coyote.common.MessageBody;
import org.apache.coyote.exception.CoyoteIOException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InputStreamRequestPublisher {

    private static final Logger log = LoggerFactory.getLogger(InputStreamRequestPublisher.class);

    private static final String HEADER_END_CONDITION = "";

    private final RequestLine requestLine;
    private final Headers headers;
    private final MessageBody messageBody;

    private InputStreamRequestPublisher(final RequestLine requestLine,
                                        final Headers headers,
                                        final MessageBody messageBody
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public static InputStreamRequestPublisher read(final InputStream inputStream) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final RequestLine requestLine = RequestLinePublisher
                    .read(readRequestLine(reader))
                    .toRequestLine();

            final Headers headers = RequestHeaderPublisher
                    .read(readHeaderValues(reader))
                    .toHeaders();

            if (isContentLengthIsNullOrWrong(headers)) {
                return createEmptyMessageBody(requestLine, headers);
            }
            return createWithMessageBody(reader, requestLine, headers);
        } catch (IOException | NullPointerException e) {
            log.warn(e.getMessage(), e);
            throw new CoyoteIOException("InputStream을 생성하던 도중에 예외가 발생하였습니다.");
        }
    }

    private static String readRequestLine(final BufferedReader br) throws IOException {
        final String requestLineValue = br.readLine();
        return requestLineValue;
    }

    private static List<String> readHeaderValues(final BufferedReader br) throws IOException {
        final List<String> headersWithValue = new ArrayList<>();
        String header = br.readLine();
        while (!header.equals(HEADER_END_CONDITION)) {
            headersWithValue.add(header);
            header = br.readLine();
        }

        return headersWithValue;
    }

    private static boolean isContentLengthIsNullOrWrong(final Headers headers) {
        final String contentLength = headers.getHeaderValue("Content-Length");

        return Objects.isNull(contentLength) || contentLength.chars().anyMatch(Character::isAlphabetic);
    }

    private static InputStreamRequestPublisher createEmptyMessageBody(final RequestLine requestLine, final Headers headers) {
        return new InputStreamRequestPublisher(requestLine, headers, MessageBody.empty());
    }

    private static InputStreamRequestPublisher createWithMessageBody(final BufferedReader reader, final RequestLine requestLine, final Headers headers) throws IOException {
        final String contentLength = headers.getHeaderValue("Content-Length");
        final char[] bodyBuffer = readBodyBuffer(reader, Integer.parseInt(contentLength));
        final MessageBody messageBody = RequestMessageBodyPublisher.read(bodyBuffer).toMessageBody();

        return new InputStreamRequestPublisher(requestLine, headers, messageBody);
    }

    private static char[] readBodyBuffer(final BufferedReader br, final int contentLength) throws IOException {
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return buffer;
    }

    public HttpRequest toHttpRequest() {
        return HttpRequest.of(requestLine, headers, messageBody);
    }
}
