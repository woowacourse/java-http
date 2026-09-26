package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class PercentDecoder {
    private static final char PERCENT = '%';
    private static final char PLUS = '+';
    private static final char SPACE = ' ';
    private static final int MAX_ASCII = 0x7F;
    private static final int INVALID_HEX = -1;

    private PercentDecoder() {
    }

    /** 경로용: '+'는 리터럴 '+' */
    public static String decodePath(final String raw) {
        return decode(raw, false);
    }

    /** 쿼리·form용: '+'는 공백 */
    public static String decodeForm(final String raw) {
        return decode(raw, true);
    }

    private static String decode(final String raw, final boolean plusAsSpace) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            final char c = raw.charAt(i);
            if (c == PERCENT) {
                bytes.write(readEscapedByte(raw, i));
                i += 2;
                continue;
            }
            if (c == PLUS && plusAsSpace) {
                bytes.write(SPACE);
                continue;
            }
            if (c > MAX_ASCII) {
                throw new BadRequestException("인코딩되지 않은 비ASCII 문자가 포함되어 있습니다");
            }
            bytes.write(c);
        }
        return toStrictUtf8(bytes.toByteArray());
    }

    // 1단계: %XX → 바이트
    private static int readEscapedByte(final String raw, final int percentIndex) {
        if (percentIndex + 2 >= raw.length()) {
            throw new BadRequestException("잘린 percent-encoding입니다");
        }
        final int high = hexValue(raw.charAt(percentIndex + 1));
        final int low = hexValue(raw.charAt(percentIndex + 2));
        if (high == INVALID_HEX || low == INVALID_HEX) {
            throw new BadRequestException("잘못된 percent-encoding입니다");
        }
        return (high << 4) | low;
    }

    private static int hexValue(final char c) {
        if ('0' <= c && c <= '9') return c - '0';
        if ('A' <= c && c <= 'F') return c - 'A' + 10;
        if ('a' <= c && c <= 'f') return c - 'a' + 10;
        return INVALID_HEX;
    }

    // 2단계: 바이트 → 문자, 잘못된 UTF-8이면 예외
    private static String toStrictUtf8(final byte[] bytes) {
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            throw new BadRequestException("잘못된 UTF-8 시퀀스입니다", e);
        }
    }
}
