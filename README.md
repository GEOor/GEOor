# GEOorWeb2
2022학년도 1학기 한국항공대 소프트웨어학과에서 [GEO&](http://geonspace.com/)과 진행한 산학 협력 프로젝트입니다.


## 📢 주제
HillShade 알고리즘을 통한 그림자가 지는 도로 예측


## 🔥 요구사항
- 건물 데이터 기반 예측 알고리즘 수행
- 기타 데이터들을 통한 예측 정확도 향상
- Web UI 및 기능 개선


## 🚀 진행과정
- 웹 지도 형식 변경 (WFS -> WMS)
- 태양 방위각, 고도 관련 Web 페이지 크롤링 (JAVA Selenium)
- DSM 데이터 좌표 변환 코드 개발
- HillShade 알고리즘 구현
- SHP 파일 사용 (Geotools)
- DB 설계 및 관리
- 터널, 교량 정보 추가
- UI 구현 (HTML, CSS, JavaScript)
- Web 구현 (Spring, PostgreSQL, PostGIS, Geoserver, Openlayers, VWORLD)


## 👀 HillShade란?

- HillShade란 그림자가 지는 정도를 수치적으로 표현한 것이다.

<img width="1128" alt="HillShade" src="https://user-images.githubusercontent.com/86689831/176238624-fed9192a-d4b1-4664-9ffd-8de58c0eef5d.png">


## ✔ HillShade 알고리즘

![image](https://user-images.githubusercontent.com/65909160/147638154-37e7339e-8db6-4842-b890-af6932b17319.png)

- HillShade 알고리즘을 사용하는 이유는 다음과 같다.
- 아래 사진처럼 만약 왼쪽에 태양이 떠 있고, 큰 산이 가로막고 있다면 오른쪽 도로에는 그늘이 생길 것이다.

<img width="1070" alt="그림자" src="https://user-images.githubusercontent.com/86689831/176234866-05be49f8-c08d-495f-a0e0-f8ceaad40745.png">


## ⚙ Web 서비스 아키텍처

![image](https://user-images.githubusercontent.com/65909160/147638540-e9cf2d5d-c0f7-4fbc-95e1-602469209d4c.png)

- 배경지도(VWorld 사용) 깔기
- 그 위에 HillShade 값에 따라 WFS Layer에 색 입히기
- Layer 추가 및 View 출력

<img width="1215" alt="실행 과정" src="https://user-images.githubusercontent.com/86689831/176236807-5d75f0bb-4708-4790-ad4d-735c65042cda.png">


## 🎞 실행 화면

- 주소, 날짜, 시간, 옵션을 선택하고 분석시작 버튼을 누르면 그림자가 지는 도로를 예측하여 보여준다.

<img width="1438" alt="실행화면" src="https://user-images.githubusercontent.com/86689831/176229606-6f1bfb6c-b169-4a36-92af-9922f6ebf7f6.png">


## 🛠 기술 스택

- ### **프론트엔드**

  <img alt="JavaScript" src="https://img.shields.io/badge/JavaScript-F7DF1E.svg?style=for-the-badge&logo=JavaScript&logoColor=white"/> <img alt="Openlayers" src="https://img.shields.io/badge/Openlayers-1F6B75.svg?style=for-the-badge&logo=Openlayers&logoColor=%2361DAFB"/>


- ### **백엔드**

    <img alt="Spring" src="https://img.shields.io/badge/Spring-6DB33F.svg?style=for-the-badge&logo=Spring&logoColor=white"/>
    <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-4169E1.svg?style=for-the-badge&logo=PostgreSQL&logoColor=white"/>
    <img alt="Spring Boot" src="https://img.shields.io/badge/Spring Boot-6DB33F.svg?style=for-the-badge&logo=SpringBoot&logoColor=white"/>
    <img alt="JDBC" src="https://img.shields.io/badge/JDBC-007396.svg?style=for-the-badge&logo=Java&logoColor=%2361DAFB"/> <img alt="Geoserver" src="https://img.shields.io/badge/GeoServer-10A0CC.svg?style=for-the-badge"/>


- ### **형상 관리**

    <img alt="Git" src="https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white"/>
    <img alt="GitHub" src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"/> <img alt="Notion" src="https://img.shields.io/badge/Notion-000000.svg?style=for-the-badge&logo=Notion&logoColor=white"/>
