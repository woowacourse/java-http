# 🐱 톰캣 구현하기 4단계 - 동시성 확장하기

## 🚀 미션 설명

HTTP 서버를 구현한 코드의 복잡도가 높아졌다.

적절한 클래스를 추가하고 역할을 맡겨서 코드 복잡도를 낮춰보자.

## ⚙️ 기능 요구 사항

### 1. Executors 로 Thread Pool 적용

Connector 클래스의 void process(final Socket connection) 메서드에서 요청마다 스레드를 새로 생성하고 있다.

Connector 클래스에서 Executors 클래스를 사용해서 ExecutorService 객체를 만들어보자.

스레드 갯수는 maxThreads 라는 변수로 지정한다.

### 2. 동시성 컬렉션 사용하기

SessionManager 클래스에서 Session 컬렉션은 여러 스레드가 동시에 접근할 수 있다.

그러다보니 Session 컬렉션에 여러 스레드가 동시에 접근하여 읽고 쓰다보면 스레드 안정성을 보장하기 어렵다.

동시성 컬렉션(Concurrent Collections)을 적용해서 스레드 안정성과 원자성을 보장해보자.

## 🖊 체크리스트

- [x] Executors 로 만든 ExecutorService 객체를 활용하여 스레드 처리를 하고 있다.

## 🖥 기능 목록

- [x] Executors 로 Thread Pool 적용
- [x] 동시성 컬렉션 사용하기

## 추가 기능 구현

### 피드백

- [x] Query 객체에서 value 를 가져오는 부분 예외처리
- [x] 프린트문 제거
- [x] GET, POST 메서드가 아닌 요청에 대한 예외 처리
- [x] 이미 로그인한 사용자가 로그인 페이지에 접근했을 때 /index.html 로 리다이렉트 하는 기능 추가

#### 1차 피드백

- [ ] GET, POST 이외에 throw 처리하기, ResponseEntity 바로 리턴하기!
- [ ] exception을 if나 try로 잡지 않고 핸들러처럼 처리하기
    - [ ] getclass() 활용
- [ ] 쿠키 있을 때, 쿠키 내부 유저 값 검증하기
