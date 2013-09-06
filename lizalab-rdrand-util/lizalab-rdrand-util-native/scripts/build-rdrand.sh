#! /bin/bash
pwd
cd target
tar -xvzf ../../src/main/resources/librdrand-1.1.tar.gz
cd librdrand-1.1
./configure
make
cp librdrand.a ../
cp config.h ../custom-javah
cp rdrand.h ../custom-javah
cd ..
rm -rf librdrand-1.1
