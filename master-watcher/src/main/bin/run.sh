#!/bin/sh

CMDNAME=`basename $0`
if [ $# -ne 2 ]; then
  echo "Usage: $CMDNAME [Pull/Req URL] [Branch Name]" 1>&2
  exit 1
fi

# Logfile
LOG_FILE=log.txt

# Java
JAVA_CMD=java
JAR_FILE=master-watcher-1.0.0.jar

# Execute
echo "[Start master-watcher]" `date +"%Y-%m-%d %H:%M:%S"` >> ${LOG_FILE}

${JAVA_CMD} -jar ${JAR_FILE} $1 $2 >> ${LOG_FILE} 2>&1
EXIT_CODE=$?

echo "[Finish master-watcher]" `date +"%Y-%m-%d %H:%M:%S"` "exit=${EXIT_CODE}" >> ${LOG_FILE}
