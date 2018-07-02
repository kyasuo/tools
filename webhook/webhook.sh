#!/bin/sh

# Java
JAVA_CMD=java
WAR_FILE=target/webhook-1.0.0-SNAPSHOT.war

# Server configuration
SERVER_PORT=1999

# IRC configuration
IRC_SERVER=
IRC_PORT=6667
IRC_NAME=webhook
IRC_CHANNEL=#talk
IRC_ENCODING=ISO-2022-JP

${JAVA_CMD} -jar ${WAR_FILE}       \
  --server.port=${SERVER_PORT}     \
  --irc.server=${IRC_SERVER}       \
  --irc.port=${IRC_PORT}           \
  --irc.name=${IRC_NAME}           \
  --irc.channel=${IRC_CHANNEL}     \
  --irc.encoding=${IRC_ENCODING}
