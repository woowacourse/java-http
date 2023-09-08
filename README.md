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

### 1. HTTP Status Code 302

- [x] 로그인에 따라 다른 응답 반환

### 2. Post 방식 회원가입

### 3. Cookie에 JSESSIONID 값 저장

### 4. Session 구현

- [x] 세션 클래스 구현
- [x] 연결

## 3단계

- [x] HttpRequest 클래스 구현
- [x] HttpResponse 클래스 구현
    - [ ] QueryParser 구현
    - [ ] Session
- [ ] Controller 추가
    - [ ] Controller 인터페이스 추가
    - [ ] AbstractController 추가
    - [ ] LoginController 추가
    - [ ] RegisterController 추가
    - [ ] ResourceController 추가
    - [ ] HomeController 추가
- [ ] if문 분기 삭제
