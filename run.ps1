param([uint32] $minDelayMs = 20, [uint32] $readTimeoutMs = 1000, [double] $failureRate = 0.2,
    [double] $zipkinSamplerPercentage = 1.0,
    [boolean] $retry = $true, [uint32] $retryIntervalMs = 100,
    [string[]] $apps=@("zipkin","order-processor","payment-gateway","inventory-reservation","shipment"))

function start-app($name) {
    invoke-expression "cmd /c start powershell -Command { [System.Threading.Thread]::CurrentThread.CurrentUICulture = 'en-US'; [System.Threading.Thread]::CurrentThread.CurrentCulture = 'en-US'; `$env:RETRY_INTERVAL_MS='$retryIntervalMs'; `$env:RETRY='$retry'; `$env:ZIPKIN_BASE_URL='http://localhost:9411/'; `$env:USE_SERVICE_BASED_URLS='false';  `$env:ZIPKIN_SAMPLER_PERCENTAGE=$zipkinSamplerPercentage; `$env:MIN_DELAY_MS=$minDelayMs; `$env:READ_TIMEOUT_MS=$readTimeoutMs; `$env:FAILURE_RATE_TO_SIMULATE=$failureRate; cd $name; ./gradlew --no-daemon clean build; java -Xmx200m -jar ./build/libs/$name-0.0.1-SNAPSHOT.jar; pause }"
}

if ($apps.contains("zipkin")) {
    invoke-expression "cmd /c start powershell -Command { java -Xmx1024m -jar zipkin.jar }"
}
foreach ($app in $apps) {
    if ($app -ne "zipkin") {
        start-app($app)
    }
}
