set -e
cd "$(dirname "$0")"

echo "====== GRADLE ======"
time ./gradlew --console=plain clean shadowJar
time ./containerBuild.sh
