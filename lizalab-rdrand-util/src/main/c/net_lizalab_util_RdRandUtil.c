/*
 * Copyright 2013 Hemant Padmanabhan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/*
 * Class:     net_lizalab_util_RdRandUtil
 * Method:    nextInt
 * Signature: ([II)I
 */
JNIEXPORT jint JNICALL Java_net_lizalab_util_RdRandUtil_nextInt
  (JNIEnv *env, jclass cls, jintArray num, jint bits) {
	union {
		unsigned char buffer[bits];
		int i;
	} number;
	int r = rdrand_get_bytes(bits, number.buffer);
	if ( r == RDRAND_SUCCESS ) {
		int numArr[1] = {number.i};
		(*env)->SetIntArrayRegion(env, num, 0, 1, (jint*) numArr);
	}
	return r;
}
