#! /bin/bash
pwd
cd target
mkdir -p jni
tar -xvzf ../src/main/resources/librdrand-1.1.tar.gz
cd librdrand-1.1
./configure
make
cp librdrand.a ../jni
cp config.h ../jni
cp rdrand.h ../jni
cd ..
rm -rf librdrand-1.1
