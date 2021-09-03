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

<br/>

## CSS 지원하기

- [x] 클라이언트에서 요청하면 CSS 파일도 제공하도록 수정
    - CSS의 경우 요청 헤더의 확장자 || Accept를 활용하여, 응답 헤더의 Content-Type을 text/css로 전송
```http request
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

<br/>

## Cookie에 JSESSIONID 값 저장하기

- 로그인에 성공하면 쿠키와 세션을 사용해서 로그인 상태 유지
    - HTTP는 stateless
    - 따라서, 세션을 활용해서 서버에 로그인 여부 저장
- Java 진영에서 세션 ID를 전달하는 이름으로 `JSESSIONID` 사용
- 서버에서 HTTP Response를 전달할 때 1. `Set-Cookie`를 추가하고 2. `JSESSION={value}` 형태로 값을 전달하면, 클라이언트의 Request Header의 `Cookie` 필드에 값이 추가 O
- e.g. 서버로부터 쿠키가 설정된 클라이언트의 HTTP Request Header
```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
```
<br/>

- [x] Cookie 클래스 추가
- HTTP Request Header의 `Cookie`에 `JSESSIONID`가 없는 경우
    - [x] HTTP Response Header에 `Set-Cookie` 필드 추가
    - [x] 해당 필드에 `JSESSIONID={value}` 형태로 값 설정
```http request
HTTP/1.1 200 OK
Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
Content-Length: 5571
Content-Type: text/html;charset=utf-8;
```

<br/>

## Session 구현하기

- [x] 로그인에 성공하면 HttpSession 객체 값으로 User 객체 저장
- [x] 로그인된 상태에서 HTTP GET 요청으로 `/login` 페이지에 접근하면 `index.html`로 리다이렉트
    - [x] 쿠키에서 전달받은 `JSESSIONID` 값으로 로그인 여부 체크
