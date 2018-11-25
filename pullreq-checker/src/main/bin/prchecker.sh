#!/bin/sh

# Logfile
LOG_FILE=log.txt

# Java
JAVA_CMD=java
JAR_FILE=pullreq-checker-1.0.0.jar

# Execute
echo "[Start pullreq-checker]" `date +"%Y-%m-%d %H:%M:%S"` >> ${LOG_FILE}

${JAVA_CMD} -jar ${JAR_FILE} >> ${LOG_FILE} 2>&1
EXIT_CODE=$?

echo "[Finish pullreq-checker]" `date +"%Y-%m-%d %H:%M:%S"` "exit=${EXIT_CODE}" >> ${LOG_FILE}
