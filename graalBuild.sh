cd "$(dirname "$0")"
set -e

#
echo "====== JAVA VERSION ======"
java -version
echo "====== GRAAL VERSION ======"
native-image --version
echo "====== BUILD native image ======"
#--static --libc=musl \
time native-image \
    -H:+ReportExceptionStackTraces \
    -jar build/libs/bootstrap.jar  \
    --initialize-at-build-time=org.slf4j \
    --initialize-at-run-time=io.netty \
    --initialize-at-build-time=scala.collection.immutable.VM \
    --initialize-at-build-time=scala.runtime.Statics$VM \
    --initialize-at-build-time=org.slf4j.LoggerFactory \
    --initialize-at-build-time=org.slf4j.impl.StaticLoggerBinder \
    --initialize-at-build-time=org.apache.lucene.util.RamUsageEstimator \
    --initialize-at-build-time=org.apache.lucene.util.Constants \
    --initialize-at-build-time=java.sql.DriverManager \
    --initialize-at-run-time=jdk.xml.internal.SecuritySupport \
    --initialize-at-run-time=com.sun.org.apache.xerces \
    --initialize-at-build-time=com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator \
    --initialize-at-run-time=jdk.xml.internal.JdkXmlUtils \
    --initialize-at-run-time=com.sun.org.apache.xerces.internal.impl.XMLEntityManager$EncodingInfo \
    --initialize-at-run-time=com.sun.org.apache.xerces.internal.impl.XMLEntityManager \
    --initialize-at-run-time=javax.xml.parsers.FactoryFinder \
    --rerun-class-initialization-at-runtime=javax.net.ssl.SSLContext,sun.security.provider.NativePRNG \
    --trace-class-initialization=jdk.xml.internal.SecuritySupport \
    --no-server \
    --no-fallback \
    --allow-incomplete-classpath \
    --report-unsupported-elements-at-runtime \
    --enable-http --enable-https --enable-url-protocols=http,https \
    -Djava.net.preferIPv4Stack=true

