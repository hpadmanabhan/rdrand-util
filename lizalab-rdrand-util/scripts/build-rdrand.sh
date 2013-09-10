#! /bin/bash
#
# Builds Intel's RdRand Library and places generated artifacts
# where the JNI native build expects to find them.
#
echo "[INFO] Current Working Directory:"
pwd

echo "[INFO] Switching Working Directory to target .."
cd target

echo "[INFO] Making jni directory to place artifacts required by JNI native build .."
mkdir -p jni

echo "[INFO] Extracting RdRand Library:"
tar -xvzf ../src/main/resources/librdrand-1.1.tar.gz

echo "[INFO] Switching Working Directory to extracted RdRand Library .."
cd librdrand-1.1

echo "[INFO] Configuring RdRand build .."
./configure

echo "[INFO] Building RdRand .."
make

echo "[INFO] Copying artifacts required by JNI native build to target/jni .."
cp librdrand.a ../jni
cp config.h ../jni
cp rdrand.h ../jni

echo "[INFO] Cleaning up .."
cd ..
rm -rf librdrand-1.1

echo "[INFO] Done."
