get_pid() {
  PID=`ps ax | grep java | grep $jarFile | awk '{print $1}'`
}

is_pid_exist() {
  checkPid=$1
  ps -p $checkPid | grep $checkPid | wc -l | tr -d '[:space:]'
}


PROG_NAME=$0
ACTION=$1
VERSION=$2

SPRING_ACTIVE_ENV="prod"
app="z-dtx-discover"
workdir="/home/z-dtx-discover"
jarFile="${app}-$VERSION.jar"
jarFileName="${app}-$VERSION"
PIDFILE="$workdir/run/pidfile"

stop() {
  if [ ! -f ${PIDFILE} ]; then
    echo "found no process has been start"
  else
    now=`date +%s`
    PID=`cat $PIDFILE`
    echo "found process ${PID} exist, killing $jarFile"
    kill $PID
    isPidExist=`is_pid_exist ${PID}`
    counter=10
    while [ "$isPidExist" != "0" ]
    do
        if (( counter < 0)); then
            kill -9 $PID && echo "${PID} exit fail, force to kill it"
            break
        fi
        counter=$((counter - 1))
        echo "wait for exit"
        sleep 1
        isPidExist=`is_pid_exist ${PID}`
    done
    echo "${PID} exit success"
    rm $PIDFILE
  fi
}

initDirs() {
  mkdir -p $workdir/run
}

start() {
  initDirs
  springActiveEnv="${SPRING_ACTIVE_ENV}"
  if [ "$springActiveEnv" = '' ] ; then
    springActiveEnv="default"
  fi

  echo "starting $jarFile, env=${springActiveEnv}"
  cd $workdir;
  nohup java -jar ${jarFile} --spring.profiles.active=$springActiveEnv -Duser.timezone=GMT+08 > /dev/null 2>&1 &
  sleep 1
  get_pid

  if [ -z ${PID} ]; then
    echo "fail to starting $jarFile"
    return 2
  else
    echo "$PID" > $PIDFILE
    echo "start success"
    return 0
  fi
}

backup(){
  cd $workdir
  mkdir -p $workdir/backup
  cur_sysTime=`date +%Y%m%d%H%M%S`
  cp $jarFile ./backup/${jarFileName}-${cur_sysTime}.jar
  echo "copy $jarFile to ./backup/${jarFileName}-${cur_sysTime}.jar"
  cd backup
  ls
}

usage() {
  echo "Usage: $PROG_NAME {start|stop|backup} {e.g. 1.0.0-SNAPSHOT}"
  exit 1;
}

case "$ACTION" in
  start)
    stop
    start
  ;;
  stop)
    stop
  ;;
  backup)
    backup
  ;;
  *)
    usage
  ;;
esac
