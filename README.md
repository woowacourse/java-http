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

<br/>

## HTTP Status Code 302

- 회원을 조회해서 로그인에 성공
    - [x] /index.html로 리다이렉트
    - [x] 응답 헤더에 Http Status Code를 302로 반환
- 회원을 조회해서 로그인에 실패
    - [x] /401.html로 리다이렉트

<br/>

## POST 방식으로 회원가입

- [x] http://localhost:8080/register 접속하면 register.html 조회
    - 회원가입 페이지 조회 -> GET 방식
    - 회원가입 버튼 클릭 -> POST 방식
        - [x] InMemoryUserRepository에서 save()를 사용해서 회원가입 완료 처리
        - [x] 회원가입을 완료하면 index.html 리다이렉트
```http request
POST /register HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Content-Length: 80
Content-Type: application/x-www-form-urlencoded
Accept: */*

account=gugu&password=password&email=hkkang%40woowahan.com
```
- [x] 로그인도 버튼 클릭할 때 GET 방식이 아닌, POST 방식으로 전송하도록 변경
    - [x] login.html도 form 태그 수정
