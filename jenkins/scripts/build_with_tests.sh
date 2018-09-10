#!/bin/bash

MODULES=(
    alerts-management
    service-layer
    long-term-persistence
    triggered-alerts
)

# Export needed env vars
export kafka_bootstrap_servers=deti-engsoft-01.ua.pt:9092
export mongo_host=deti-engsoft-02.ua.pt
export datasource_postgres=jdbc:postgresql://deti-engsoft-01.ua.pt:5432/es202
export datasource_postgres_alerts=jdbc:postgresql://deti-engsoft-01.ua.pt:5432/es202
export datasource_postgres_triggered_alerts=jdbc:postgresql://deti-engsoft-01.ua.pt:5432/es202
export datasource_username=es202
export datasource_password=61ecbc41cdae

URL="http://0.0.0.0:8089/alerts/alerts/"

for i in ${!MODULES[@]}; do

    # Go to module project folder
    cd ${MODULES[$i]}
     
    # Clean old build files
    rm -rf target

    # Test and build module 
    mvn clean package
    
    # Try to test and build module again, if it failed
    if [[ "$?" -ne 0 ]] ; then
        # Clean old build files
        rm -rf target

        # Test and build module 
        mvn clean package

        # If it fails again, something went wrong, so we need to stop the build
        if [[ "$?" -ne 0 ]] ; then
            echo "Something went wrong testing ${MODULES[$i]}" 1>&2
            exit 1
        fi

    fi

    # Go again to project root folder
    cd ..
    
    # Start docker environment
    if [[ ${MODULES[$i]} == "alerts-management" ]]; then
        docker-compose -f docker-compose-tests.yml up -d
        
        # Wait for alerts module to be active
        while true
        do
        	if curl --output /dev/null --silent --head --fail "$URL"; then
                break
            fi
            sleep 1
        done
    fi
done

# Stop docker environment
docker-compose -f docker-compose-tests.yml down
