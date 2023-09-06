# 톰캣 구현하기

# 기능 요구사항

## 1단계

### 1. GET /index.html 응답하기

- [x] IOStreamTest 학습 테스트 진행
- [x] inputStream 에서 header 읽기
    - [x] 무한 루프 점검
    - [x] 헤더별 파싱
- [x] header 에서 url 파싱
- [x] url에 맞는 resource 반환

### 2. CSS 지원하기

- [x] MIME 형식 지원

### 3. Query String 파싱

- [x] Query String 파싱
- [x] 로그 찍기

## 2단계

- [x] 그냥 하드 코딩 하자

### 1. HTTP Status Code 302

- [x] 로그인에 따라 다른 응답 반환

### 2. Post 방식 회원가입

### 3. Cookie에 JSESSIONID 값 저장

### 4. Session 구현

- [x] 세션 클래스 구현
- [x] 연결

### 필요한 것 생각

요청

- /

- /index.html or /index

- css/styles.css etc.

- /login or /login.html

# 리팩터링 생각

## Request는 어떻게 구성되어야 하는가

- RequestLine, RequestHeader, RequestBody로 나누자.
    - RequestLine : HttpMethod, RequestUri, HttpVersion
    - RequestHeader : ContentLength, Cookie 등
    - RequestBody

## 언제 Cookie에서 JSESSIONID를 조회할 것인가

- 권한에 따라 다르다.
    - 권한이 필요하지 않는 URI
        - GET /
        - GET /css/styles.css 등
        - GET /register
        - POST /register (with body)
    - 권한이 필요한 URI
        - GET /index
    - 권한이 필요하지 않지만, 확인해야 하는 URI
        - GET /login
        - GET /login? (with query string)
        - POST /login (with body)

- Http Method에 따라 구분될 수 있는가?
    - 아니다. GET, POST, DELETE, PUT, PATCH 등 모두 권한에 따라 다르다.

- 그렇다면 uri 따라 분기처리를 해야 한다.

## Response는 어떻게 구성되어야 하는가
