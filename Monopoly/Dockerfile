FROM openjdk:17

# Set working directory
WORKDIR /app

# Copy all project files
COPY . /app

# Compile Java code
RUN javac -encoding UTF-8 -d shared/out $(find shared/src -name "*.java") && \
    javac -encoding UTF-8 -cp "shared/out:libs/*" -d server/out $(find server/src -name "*.java")

# Set port
EXPOSE 5100

# Start the server
CMD java -cp "server/out:shared/out:libs/*" monopoly.server.GameServer
