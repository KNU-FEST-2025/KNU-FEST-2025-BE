#!/bin/bash

CONTAINER='knufest-2025_DB_Replica'
USER='root'
DATABASE=$(docker exec $CONTAINER printenv SOURCE_DB)
PW=$(docker exec $CONTAINER printenv MYSQL_ROOT_PASSWORD)
BACKUP_PATH='/00_maintenance/z_backup/knufest-2025_DB_Replica'
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
FILENAME="${DATABASE}_${TIMESTAMP}.sql"
BACKUP_DEST="$BACKUP_PATH/$FILENAME"

docker exec $CONTAINER sh -c "MYSQL_PWD=$PW mysqldump -uroot --single-transaction --routines --triggers --events $DATABASE" > $BACKUP_DEST
