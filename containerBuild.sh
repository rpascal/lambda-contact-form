docker run --rm --mount type=bind,source="$(pwd)"/,target=/builddir \
 --name buildgradle \
 graal_vm_build_env:latest /bin/sh "/builddir/graalBuild.sh"