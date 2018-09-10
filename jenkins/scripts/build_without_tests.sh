#!/bin/bash

MODULES=(
    service-layer
    long-term-persistence
    alerts-management
    triggered-alerts
)

for i in ${!MODULES[@]}; do
    # Go to module project folder
    cd ${MODULES[$i]}
    
    # Clean old build files
    rm -rf target

    # Build without tests 
    mvn -X -Dmaven.test.skip=true -Dmongo_host=127.0.0.1 -Dkafka_bootstrap_servers=127.0.0.1:9092 -Ddatasource_postgres_alerts=jdbc:postgresql://127.0.0.1:5432/ -Ddatasource_postgres=jdbc:postgresql://127.0.0.1:5432/ -Ddatasource_postgres_triggered_alerts=jdbc:postgresql://127.0.0.1:5432/ clean package
    
    # Go again to project root folder
    cd ..
done
