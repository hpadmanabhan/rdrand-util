##
# Copyright 2013 Hemant Padmanabhan
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
##

#! /bin/bash
#
# build-rdrand.sh
#
# Builds Intel's RdRand Library and places generated artifacts
# where the JNI native build expects to find them.

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
# We run the build manually instead of using the generated Makefile
# because we need to compile with -fPIC to generate a shared library
# and we do not need to compile the extra test stuff.
gcc -g -O2 -O2 -fPIC -c rdrand.c
ar rcs librdrand.a rdrand.o

echo "[INFO] Copying artifacts required by JNI native build to target/jni .."
cp librdrand.a ../jni
cp config.h ../jni
cp rdrand.h ../jni

echo "[INFO] Cleaning up .."
cd ..
rm -rf librdrand-1.1

echo "[INFO] Done."
