# HTTP 서버 구현하기

<br/>

## GET /index.html 응답하기

- [x] 첫번째 라인(Request URI) 읽기
    - [x] HTTP Request의 첫번째 라인에서 Request URI 추출
        - [x] `line.split(" ")`을 활용해서 문자열 분리
- [x] 나머지 라인 읽기
    - [x] IOStreamTest를 참고해서 HTTP Request 읽기
    - [x] line이 null인 경우 예외 처리 -> 무한 루프 방지
        - [x] `if (line == null) { return; }`
    - [x] Header 마지막은 `while (!"".equals(line)) {}`으로 확인 가능
- [x] FileTest를 참고해서 요청 URL에 해당되는 파일을 resources 디렉토리에서 읽기

<br/>

## Query String 파싱

- [x] HTTP Request의 첫번째 라인에서 Request URI 추출
- [x] login.html 파일에서 태그에 name 추가
- [x] Request URI에서 접근 경로와 `이름=값`으로 전달되는 데이터를 추출해서 User 객체 생성
- [x] InMemoryUserRepository를 사용해서 미리 가입되어 있는 회원을 조회하고 로그 확인
