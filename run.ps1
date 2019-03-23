param([uint32] $minDelayMs = 20, [uint32] $readTimeoutMs = 1000, [double] $failureRate = 0.2,
    [double] $zipkinSamplerPercentage = 1.0,
    [string[]] $apps=@("zipkin","order-processor","payment-gateway","inventory-reservation","shipment"))

function start-app($name) {
    invoke-expression "cmd /c start powershell -Command { [System.Threading.Thread]::CurrentThread.CurrentUICulture = 'en-US'; [System.Threading.Thread]::CurrentThread.CurrentCulture = 'en-US'; `$env:ZIPKIN_SAMPLER_PERCENTAGE=$($zipkinSamplerPercentage.toString().replace(",", ".")); `$env:MIN_DELAY_MS=$minDelayMs; `$env:READ_TIMEOUT_MS=$readTimeoutMs; `$env:FAILURE_RATE_TO_SIMULATE=$failureRate; cd $name; ./gradlew --no-daemon clean build; java -Xmx200m -jar ./build/libs/$name-0.0.1-SNAPSHOT.jar; pause }"
}

if ($apps.contains("zipkin")) {
    invoke-expression "cmd /c start powershell -Command { java -Xmx1024m -jar zipkin.jar }"
}
foreach ($app in $apps) {
    if ($app -ne "zipkin") {
        start-app($app)
    }
}
