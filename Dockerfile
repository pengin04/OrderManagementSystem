FROM maven:3-eclipse-temurin-21 AS build

# 作業ディレクトリを設定（重要！）
WORKDIR /app

# プロジェクト全体を /app にコピー
COPY . .

# ビルド
RUN mvn clean package -Dmaven.test.skip=true

# 実行用ステージ
FROM eclipse-temurin:21-alpine
WORKDIR /app

# build ステージから jar をコピー
COPY --from=build /app/target/*.jar app.jar

# アプリを実行
ENTRYPOINT ["java","-jar","app.jar"]
