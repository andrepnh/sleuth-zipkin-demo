#!/bin/bash

minDelayMs=20
readTimeoutMs=1000
failureRate=0.2
zipkinSamplerPercentage=1.0

function start-app {
    app=$1
    ttab "jenv-export-java-home && export ZIPKIN_BASE_URL=http://localhost:9411/ && export USE_SERVICE_BASED_URLS=false && export ZIPKIN_SAMPLER_PERCENTAGE=$zipkinSamplerPercentage && export MIN_DELAY_MS=$minDelayMs && export READ_TIMEOUT_MS=$readTimeoutMs && export FAILURE_RATE_TO_SIMULATE=$failureRate && cd $app && ./gradlew --no-daemon clean build && java -Xmx200m -jar ./build/libs/$app-0.0.1-SNAPSHOT.jar"
}

ttab java -Xmx1024m -jar zipkin.jar
start-app "order-processor" 
start-app "payment-gateway" 
start-app "inventory-reservation" 
start-app "shipment"
