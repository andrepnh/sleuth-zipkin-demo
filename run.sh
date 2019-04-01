#!/bin/bash
set -o errexit -o pipefail -o noclobber -o nounset

# https://stackoverflow.com/questions/192249/how-do-i-parse-command-line-arguments-in-bash/14203146
LONGOPTS="min-delay:,read-timeout:,failure-rate:,sampler-percentage:,no-retry,retry-interval:"

# -use ! and PIPESTATUS to get exit code with errexit set
# -temporarily store output to be able to check for errors
# -activate quoting/enhanced mode (e.g. by writing out “--options”)
# -pass arguments only via   -- "$@"   to separate them correctly
! PARSED=$(/usr/local/Cellar/gnu-getopt/1.1.6/bin/getopt --options="" --longoptions="$LONGOPTS" --name "$0" -- "$@")
if [[ ${PIPESTATUS[0]} -ne 0 ]]; then
    # e.g. return value is 1
    #  then getopt has complained about wrong arguments to stdout
    exit 2
fi
# read getopt’s output this way to handle the quoting right:
eval set -- "$PARSED"

minDelayMs=20
readTimeoutMs=1000
failureRate=0.2
zipkinSamplerPercentage=1.0
retry="true"
retryIntervalMs=500
# now enjoy the options in order and nicely split until we see --
while true; do
    case "$1" in
        --min-delay)
            minDelayMs="$2"
            shift 2
            ;;
        --read-timeout)
            readTimeoutMs="$2"
            shift 2
            ;;
        --failure-rate)
            failureRate="$2"
            shift 2
            ;;
        --sampler-percentage)
            zipkinSamplerPercentage="$2"
            shift 2
            ;;
        --no-retry)
            retry="false"
            shift
            ;;
        --retry-interval)
            retryIntervalMs="$2"
            shift 2
            ;;
        --)
            shift
            break
            ;;
        *)
            echo "Programming error"
            exit 3
            ;;
    esac
done

# handle non-option arguments
apps="zipkin order-processor inventory-reservation payment-gateway shipment"
if [[ $# -ne 0 ]]; then
    apps=$@
fi

function start-app {
    app=$1
    ttab "echo -ne '\033]0;'$app'\007' && jenv-export-java-home && export ZIPKIN_BASE_URL=http://localhost:9411/ && export RETRY_INTERVAL_MS=$retryIntervalMs && export RETRY=$retry && export USE_SERVICE_BASED_URLS=false && export ZIPKIN_SAMPLER_PERCENTAGE=$zipkinSamplerPercentage && export MIN_DELAY_MS=$minDelayMs && export READ_TIMEOUT_MS=$readTimeoutMs && export FAILURE_RATE_TO_SIMULATE=$failureRate && cd $app && ./gradlew --no-daemon build && java -Xmx200m -jar ./build/libs/$app-0.0.1-SNAPSHOT.jar"
}

for app in $apps; do
    if [[ "$app" == "zipkin" ]]; then
        ttab "echo -ne '\033]0;'$app'\007' && java -Xmx1024m -jar zipkin.jar"
    else 
        start-app $app
    fi
done
