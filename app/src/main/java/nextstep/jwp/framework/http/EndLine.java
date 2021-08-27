package nextstep.jwp.framework.http;

public class EndLine extends AbstractParsingLine {

    @Override
    public boolean canParse() {
        return false;
    }

    @Override
    public ParsingLine parse(String line) {
         throw new UnsupportedOperationException("파싱이 종료된 리퀘스트입니다.");
    }
}
