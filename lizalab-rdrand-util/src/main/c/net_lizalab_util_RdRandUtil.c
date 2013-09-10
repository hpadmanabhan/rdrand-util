#include <net_lizalab_util_RdRandUtil.h>
#include <rdrand.h>
#include <stdio.h>

/*
 * Class:     net_lizalab_util_RdRandUtil
 * Method:    verifyNative
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_lizalab_util_RdRandUtil_verifyNative
  (JNIEnv *env, jclass cls) {
	uint32_t u32;
	int r = rdrand_32(&u32, 10);
	return r;
}

/*
 * Class:     net_lizalab_util_RdRandUtil
 * Method:    nextBytesNative
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_net_lizalab_util_RdRandUtil_nextBytesNative
  (JNIEnv *env, jclass cls, jbyteArray bytes, jint size) {
	unsigned char buffer[size];
	int r = rdrand_get_bytes(size, buffer);
	if ( r == RDRAND_SUCCESS ) {
		(*env)->SetByteArrayRegion(env, bytes, 0, size, (jbyte*) buffer);
	}
	return r;
}
