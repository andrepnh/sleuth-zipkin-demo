function start-app($name) {
    invoke-expression "cmd /c start powershell -Command { cd $name; ./gradlew --no-daemon clean build; java -Xmx200m -jar ./build/libs/$name-0.0.1-SNAPSHOT.jar  }"
}
start-app('order-processor')
start-app('payment-gateway')
start-app('fulfillment-plan')
start-app('inventory-reservation')
start-app('shipment')
