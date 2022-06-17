FROM ghcr.io/graalvm/graalvm-ce:ol8-java11-21.0.0
RUN microdnf install libgcc
RUN microdnf install gcc
#RUN yum update -y && yum install -y libgcc gcc musl-dev
RUN gu install native-image
