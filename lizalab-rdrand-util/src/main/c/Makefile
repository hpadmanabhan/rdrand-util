# Makefile for RdRand Java Utility JNI Implementation.
# 
# Generates the JNI header from the class with the native calls.
# Compiles the JNI implementation linking it to rdrand and creating
# a shared library.

TARGET_DIR := ../../../target/classes
TARGET := $(TARGET_DIR)/librdrandjutil.so

WORKING_DIR := $(TARGET_DIR)/../jni

JAVA_BUILD_DIR := $(TARGET_DIR)

JAVA_HOME ?= /usr/java/default

INCLUDES := -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux -I $(JAVA_HOME)/include/darwin -I $(WORKING_DIR)

# classpath for javah
ifdef CLASSPATH
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR):$(CLASSPATH)
else
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR)
endif

all: $(TARGET)

$(TARGET): net_lizalab_util_RdRandUtil.c $(WORKING_DIR)/config.h $(WORKING_DIR)/rdrand.h $(WORKING_DIR)/net_lizalab_util_RdRandUtil.h
	@echo "[INFO] Compiling JNI implementation and linking shared library .."
	gcc -fPIC -g -O2 -c net_lizalab_util_RdRandUtil.c $(INCLUDES)
	gcc net_lizalab_util_RdRandUtil.o -shared -o $(TARGET) -L$(WORKING_DIR) -lrdrand

$(WORKING_DIR)/net_lizalab_util_RdRandUtil.h: $(TARGET_DIR)/net/lizalab/util/RdRandUtil.class
	@echo "[INFO] Generating JNI header .."
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) net.lizalab.util.RdRandUtil