set -e
cd "$(dirname "$0")"

echo "====== GRADLE ======"
time ./gradlew --console=plain clean shadowJar
time ./containerBuild.sh

echo "====== ZIP Package ======"
rm bootstrap.zip
zip bootstrap.zip bootstrap
echo "====== ZIP Deploy ======"
time aws lambda update-function-code --function-name contact-form-service  --zip-file fileb://$(pwd)/bootstrap.zip
#echo "====== Invoke ======"
time aws lambda invoke --function-name contact-form-service --invocation-type RequestResponse --log-type Tail  $(date +%s).response.json
#echo "====== Logs ======"
aws lambda invoke --function-name contact-form-service  --invocation-type RequestResponse --log-type Tail  $(date +%s).response.json | jq -r '.LogResult' | base64 -d
echo "====== Complete ======"
