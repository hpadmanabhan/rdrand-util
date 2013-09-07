# Makefile for RdRand Java Utility JNI Implementation.

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
	gcc -O2 -shared -fPIC -L$(WORKING_DIR) -lrdrand $(INCLUDES) net_lizalab_util_RdRandUtil.c -o $(TARGET)

$(WORKING_DIR)/net_lizalab_util_RdRandUtil.h: $(TARGET_DIR)/net/lizalab/util/RdRandUtil.class
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) net.lizalab.util.RdRandUtil