# 톰캣 구현하기

## 1단계 - HTTP 서버 구현하기

구현 기능 목록

- [x] GET /index.html 응답하기
- [x] .css, .js 지원하기
- [x] Query String 파싱하기

리팩터링

- [x] Http11Request를 만들어 InputStream으로부터 request를 가져오는 책임 분리
- [x] Url를 enum으로 만들어 Processor 내의 분기 처리 제거
- [x] Http11Response를 만들어 response를 만드는 책임 분리
    - [x] outputStream.write() 부분까지 책임 분리
- [x] GET 이외의 요청에 대한 처리
- [x] 커스텀 예외를 만들어 상황에 보다 적합한 예외 반환
- [x] 로그인시 존재하는 유저인지 확인하는 로직 처리 고민하기
    - [x] queryString을 관리하는 책임 분리
