#!/bin/bash

if [ $# -ne 1 ]; then
  echo
  echo "Usage: $0 <replica_cont_name>"
  echo
  exit 1
fi

CONT_NAME=$1
SOURCE_ROOT_PW=$(docker exec $1 printenv MYSQL_ROOT_PASSWORD)
SOURCE_HOST=$(docker exec $1 printenv SOURCE_HOST)
SOURCE_PORT=$(docker exec $1 printenv SOURCE_PORT)
SOURCE_USER=$(docker exec $1 printenv SOURCE_USER)
SOURCE_PASSWORD=$(docker exec $1 printenv SOURCE_PASSWORD)
SOURCE_DATABASE=$(docker exec $1 printenv SOURCE_DB)

echo
echo "[1/5] Reset Replica"
docker exec $1 sh -c "MYSQL_PWD=$SOURCE_ROOT_PW mysql -uroot -e 'STOP REPLICA; RESET REPLICA ALL;'"
echo
echo "[2/5] Check Master binlog"
MASTER_STATUS=$(docker exec $SOURCE_HOST sh -c "MYSQL_PWD=$SOURCE_ROOT_PW mysql -uroot -e 'SHOW MASTER STATUS\G;'")
BINLOG_FILE=$(echo "$MASTER_STATUS" | grep File | awk '{print $2}')
BINLOG_POS=$(echo "$MASTER_STATUS" | grep Position | awk '{print $2}')
echo "Master Binlog: $BINLOG_FILE, position: $BINLOG_POS"
echo
echo "[3/5] Get Original DATA from Master"
TMP_FILE="./tmp.sql"
docker exec $SOURCE_HOST sh -c "MYSQL_PWD=$SOURCE_ROOT_PW mysqldump -uroot --single-transaction --routines --triggers --events $SOURCE_DATABASE" > $TMP_FILE
echo
echo "[4/5] Send the data to Replica"
docker exec $1 sh -c "MYSQL_PWD=$SOURCE_ROOT_PW mysql -uroot -e 'SET GLOBAL read_only=OFF;'"
docker exec -i $1 sh -c "MYSQL_PWD=$SOURCE_PASSWORD mysql -u$SOURCE_USER -D$SOURCE_DATABASE" < $TMP_FILE
rm $TMP_FILE 
echo
echo "[5/5] Backup Setting for Incremental data to Replica"
echo
docker exec -i $1 sh -c "MYSQL_PWD=$SOURCE_ROOT_PW mysql -uroot" <<EOF
  STOP REPLICA; 
  RESET REPLICA ALL; 
  CHANGE REPLICATION SOURCE TO
    SOURCE_HOST="$SOURCE_HOST",
    SOURCE_PORT=$SOURCE_PORT,
    SOURCE_USER="$SOURCE_USER",
    SOURCE_PASSWORD="$SOURCE_PASSWORD",
    SOURCE_LOG_FILE="$BINLOG_FILE",
    SOURCE_LOG_POS=$BINLOG_POS,
    GET_SOURCE_PUBLIC_KEY=1;
  START REPLICA;
EOF
docker exec $1 sh -c "MYSQL_PWD=$SOURCE_ROOT_PW mysql -uroot -e 'SET GLOBAL read_only=ON;'"
