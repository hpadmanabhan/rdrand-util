#include <net_lizalab_util_RdRandUtil.h>
#include <rdrand.h>
#include <stdio.h>

JNIEXPORT jint JNICALL Java_net_lizalab_util_RdRandUtil_verifyNative
  (JNIEnv *env, jclass cls) {
        uint32_t u32;
        int r = rdrand_32(&u32, 10);
        printf("result: %u\n", r);
        return r;
}

JNIEXPORT jint JNICALL Java_net_lizalab_util_RdRandUtil_nextBytesNative
  (JNIEnv *env, jclass cls, jbyteArray bytes, jint size) {
        unsigned char buffer[size];
        int r = rdrand_get_bytes(size, buffer);
        if ( r == RDRAND_SUCCESS ) {
                int i, j;
                printf("\nBuffer of %ld bytes:\n", (long) size);

                j= 0;
                for (i= 0; i< size; ++i) {
                        printf("%02x ", buffer[i]);
                        ++j;
                        if ( j == 16 ) {
                                j= 0;
                                printf("\n");
                        }
                        else if ( j == 8 ) printf(" ");
                }
                printf("\n");

                (*env)->SetByteArrayRegion(env, bytes, 0, size, (jbyte*) buffer);
        }
        else printf("rdrand instruction failed with code %d\n", r);
	return r;
}
