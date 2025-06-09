#!/bin/bash

# 배포 디렉토리 설정
DEPLOY_DIR="/home/ubuntu"
PROJECT_NAME="spring-roomescape-payment"
PROJECT_DIR="$DEPLOY_DIR/$PROJECT_NAME"

echo "🚀 배포 시작: $PROJECT_NAME"

cd $DEPLOY_DIR

# Git 저장소 업데이트
echo "📦 코드 업데이트 중..."
if [ -d "$PROJECT_DIR" ]; then
    cd $PROJECT_DIR
    git pull origin step2 > /dev/null 2>&1
    echo "✅ Git pull 완료"
else
    git clone -b step2 https://github.com/KoSeonJe/spring-roomescape-payment.git > /dev/null 2>&1
    if [ -d "$PROJECT_DIR" ]; then
        cd $PROJECT_DIR
        echo "✅ Git clone 완료"
    else
        echo "❌ Git clone 실패"
        exit 1
    fi
fi

# 기존 애플리케이션 프로세스 종료
echo "⏹️  기존 프로세스 종료 중..."
PID=$(ps aux | grep java | grep $PROJECT_NAME | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    kill -9 $PID > /dev/null 2>&1
    sleep 2
    echo "✅ 기존 프로세스 종료됨 (PID: $PID)"
else
    echo "ℹ️  실행 중인 프로세스 없음"
fi

# Gradle 빌드 (테스트 포함)
echo "🔨 빌드 및 테스트 중..."
./gradlew clean build > /dev/null 2>&1
echo "✅ 빌드 완료"

# JAR 파일 찾기
JAR_FILE=$(find build/libs -name "*.jar" | head -1)

# 백그라운드에서 애플리케이션 실행
echo "🚀 애플리케이션 시작 중..."
nohup java -jar $JAR_FILE > app.log 2>&1 &

echo "✅ 배포 완료: $PROJECT_NAME"
echo "📋 로그 확인: tail -f $PROJECT_DIR/app.log"
