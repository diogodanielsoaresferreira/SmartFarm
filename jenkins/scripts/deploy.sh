#!/bin/bash

MODULES=(
    service-layer
    long-term-persistence
    alerts-management
    triggered-alerts
)

FOLDERS=(
    ServiceLayer-0.0.1-SNAPSHOT
    LongTermPersistence-0.0.1-SNAPSHOT
    AlertsManagement-0.0.1-SNAPSHOT
    TriggeredAlerts-0.0.1-SNAPSHOT
)

DEPLOY_FOLDER=smartfarm_modules
DEPLOY_PATH=jenkins

MACHINE_USERNAME=es202
MACHINE_HOSTNAME=deti-engsoft-07.ua.pt

mkdir -p $DEPLOY_FOLDER

for i in ${!MODULES[@]}; do
    # Join war and build in one folder
    cp -r ${MODULES[$i]}/target/${FOLDERS[$i]} $DEPLOY_FOLDER
    cp ${MODULES[$i]}/target/${FOLDERS[$i]}.war $DEPLOY_FOLDER
done

# Copy dashboard to deploy folder
cp -r dashboard/ $DEPLOY_FOLDER

# Compress folder and send it to the deploy machine
tar -zcvf $DEPLOY_FOLDER.tgz $DEPLOY_FOLDER
scp -o StrictHostKeyChecking=no $DEPLOY_FOLDER.tgz $MACHINE_USERNAME@$MACHINE_HOSTNAME:~/$DEPLOY_PATH

# Decompress builded modules and restart docker container
ssh -o StrictHostKeyChecking=no $MACHINE_USERNAME@$MACHINE_HOSTNAME "mkdir -p $DEPLOY_PATH &&
    rm -rf $DEPLOY_PATH/$DEPLOY_FOLDER &&
    tar -zxvf $DEPLOY_PATH/$DEPLOY_FOLDER.tgz -C $DEPLOY_PATH &&
    docker restart smartfarm_tomcat_1 &&
    docker restart smartfarm_dashboard_1 &&
    rm $DEPLOY_PATH/$DEPLOY_FOLDER.tgz"

# Clean up things
rm -rf $DEPLOY_FOLDER
rm $DEPLOY_FOLDER.tgz
