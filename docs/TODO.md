# 1단계 - HTTP 서버 구현하기

### 1. GET /index.html 응답하기

- HTTP Start Line 읽기
  - HTTP Request의 첫 번째 줄을 읽는다.
- HTTP Start Line을 요소 별로 구분하기
  - HTTP method, Request Uri, HTTP version으로 구분하기
- HTTP Request 요청 url에 해당되는 파일을 resource 디렉토리에서 읽는다.

### 2. CSS 지원하기

- '.css' 파일을 받았을 경우, `Content-Type: text/html;charset=utf-8;`로 설정

### 3. Query String 파싱
