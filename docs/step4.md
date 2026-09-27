# 4단계 - 동시성 확장하기

## 완료 조건

### Thread Pool

- [x] `Connector`가 `ExecutorService`를 소유한다.
- [x] `maxThreads`로 동시에 실행할 Worker Thread 수를 제한한다.
- [x] 연결마다 새로운 Thread를 생성하지 않는다.
- [x] 연결 처리 작업을 Thread Pool에 제출한다.
- [x] `Connector` 종료 시 Thread Pool도 종료한다.

### 동시성 컬렉션

- [x] `SessionManager`의 Session 저장소가 동시 접근에 안전한 컬렉션을 사용한다.
- [ ] `Session`의 attribute 저장소가 동시 접근에 안전한 컬렉션을 사용한다.
