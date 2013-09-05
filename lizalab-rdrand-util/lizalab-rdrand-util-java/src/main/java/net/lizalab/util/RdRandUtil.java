package net.lizalab.util;

/**
 * JNI Wrapper to the RdRand library exposing available functionality
 * useful in the Java context as native functions.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public final class RdRandUtil {
	
	/**
	 * Native method to verify RdRand status. Invokes the RdRand instruction
	 * and propagates the numeric status returned.
	 * @return The numeric status code returned by RdRand instruction.
	 */
	private static native int verifyNative();
	
	/**
	 * Verifies the the availability and status of the RdRand instruction
	 * on the host.
	 * @return Status of the RdRand instruction on the host.
	 */
	public static RdRandStatus verify() {
		int result = verifyNative();
		return RdRandStatus.getStatusByCode(result);
	}
	
	/**
	 * Native method fetching the specified number of bytes in the provided
	 * byte array from RdRand.
	 * @param bytes The byte array to fill with random bytes.
	 * @param size The number of random bytes to fetch.
	 * @return Numeric status code returned by RdRand for the fetch operation.
	 */
	private static native int nextBytesNative(byte[] bytes, int size);
	
	/**
	 * Fetches random bytes from RdRand and places them into the user specified
	 * array. The number of random bytes fetched is equal to the length of the
	 * byte array.
	 * @param bytes The byte array to fill with random bytes.
	 * @throws RdRandException If RdRand returns a non-success status.
	 */
	public static void nextBytes(byte[] bytes) {
		int result = nextBytesNative(bytes, bytes.length);
		RdRandStatus status = RdRandStatus.getStatusByCode(result);
		if (status != RdRandStatus.SUCCESS) {
			throw new RdRandException(status);
		}
	}
}
