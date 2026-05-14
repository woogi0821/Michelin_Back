# 🍽️ MichelinGuide
**실시간 리뷰 기반의 미슐랭 음식점 정보 조회 및 상호작용 플랫폼**
 
---
 
## 🛠 1. Tech Stack (기술 스택)
 
### Frontend
![React](https://img.shields.io/badge/react-19-%2361DAFB?style=for-the-badge&logo=react&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/tailwindcss-v4-%2306B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![Zustand](https://img.shields.io/badge/zustand-pink?style=for-the-badge&logo=react&logoColor=white)
![React Router](https://img.shields.io/badge/react%20router-CA4245?style=for-the-badge&logo=react-router&logoColor=white)
![Axios](https://img.shields.io/badge/axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)
 
### Backend
![Spring Boot](https://img.shields.io/badge/springboot-3.5.11-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JPA/Hibernate](https://img.shields.io/badge/jpa-hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Oracle DB](https://img.shields.io/badge/oracle%20db-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Redis](https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
 
### Environment & Tools
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
 
---
 
## 📋 2. 핵심 기능 (MVP Scope)
 
### Phase 1: Core Features
- **음식점 정보 조회**
  - 미슐랭 별점, 기본 정보 (주소, 전화, 영업시간 등)
  - 지도 연동 (위치 기반 검색)
  
- **실시간 리뷰 시스템**
  - 사용자 리뷰 작성/수정/삭제
  - 별점 평가 (5단계)
  - 리뷰 상호작용 (좋아요, 댓글)
  
- **사진 업로드**
  - 리뷰에 음식/분위기 사진 첨부
  - 이미지 저장소 연동
---
 
## 🤝 3. Team Rules (7인 협업 규칙)
 
우리 프로젝트는 **'각자의 부품(컴포넌트)을 만들어 하나의 시스템으로 조립'**하는 구조입니다. 아래 규칙을 반드시 준수합니다.
 
### 1. 개인 브랜치 활성화 (Branch Naming)
- `main`이나 `dev` 브랜치에 직접 푸시(Push)하는 것은 엄격히 금지합니다.
- 반드시 **`dev-이니셜`** 형식의 개인 브랜치를 생성하여 작업합니다.
- *예시: `dev-ugi`, `dev-sjw` 등*
### 2. 코드 리뷰 필수
- 모든 PR(Pull Request)은 **최소 1명 이상의 팀원 리뷰 및 승인**이 있어야 머지할 수 있습니다.
### 3. 충돌 방지
- 공통 컴포넌트나 라우터 파일을 수정할 때는 반드시 팀원들에게 미리 공유합니다.
- 신규 엔티티 추가 또는 DB 스키마 변경 시 먼저 논의합니다.
---
 
## 🤖 4. AI Usage Rule (생산성 가이드라인)
 
AI(ChatGPT, Claude 등)를 활용한 개발을 적극 권장하지만, 다음의 원칙을 따릅니다.
 
- **코드 이해 필수:** 복사해서 붙여넣은 코드라도 **로직의 흐름과 함수별 역할**을 완벽히 이해해야 합니다.
- **설명 책임:** 본인이 올린 코드는 리뷰 시 팀원들에게 로직을 명확히 설명할 수 있어야 합니다.
- **구조 유지:** 프로젝트의 공통 스타일(Tailwind)과 폴더 구조를 해치지 않도록 프롬프트를 세심하게 조정해 주세요.
---
 
## 📊 5. 아키텍처 & DB 설계 (회의 예정)
 
### Backend Package Structure (임시)
```
backend/
├── controller/       # API 엔드포인트
├── service/          # 비즈니스 로직
├── repository/       # DB 접근 (JPA)
├── entity/           # DB 테이블 모델
├── dto/              # 요청/응답 DTO
├── config/           # 설정 (Security, Redis 등)
└── exception/        # 커스텀 예외
```
 
### 주요 엔티티 (확정 대기)
```
[ 회의 후 아래 항목들을 추가할 예정 ]
- MEMBER (사용자)
- RESTAURANT (음식점)
- REVIEW (리뷰)
- COMMENT (댓글)
- IMAGE (이미지)
- FAVORITE (찜하기)
- LIKE (좋아요)
```
 
**⚠️ 엔티티 추가 전 팀 회의 필수!**
 
---
 
## ✅ PR Merge 체크리스트
 
**코드 품질:**
- [ ] Tailwind 클래스명 컨벤션 준수
- [ ] API 응답 에러 처리 완료
- [ ] Console 에러/경고 없음
**협업:**
- [ ] 최소 1명 이상 리뷰 및 승인
- [ ] Conflict 해결 완료
**테스트:**
- [ ] 로컬 환경에서 테스트 완료
- [ ] DB 마이그레이션 필요 시 공지

 
**"명확한 역할 분담과 실시간 동기화로 완성도 높은 서비스를 만듭니다. 파이팅!"**
