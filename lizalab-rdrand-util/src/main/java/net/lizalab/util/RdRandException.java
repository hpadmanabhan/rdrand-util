package net.lizalab.util;

/**
 * Thrown when a native RdRand API call fails. Provides the specific
 * status returned by RdRand explaining the error cause.
 *  
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public final class RdRandException extends RuntimeException {

	private static final long serialVersionUID = 5768836154745211956L;
	
	/**
	 * RdRand status resulting in the runtime exception.
	 */
	private final RdRandStatus status;

	/**
	 * Initializes the exception with the specific RdRand status
	 * that resulted in the exception.
	 * @param status The RdRand status.
	 * @throws IllegalArgumentException If status provided is null or success.
	 */
	public RdRandException(RdRandStatus status) {
		// Verify a valid exception causing RdRand status is specified.
		if (status == null) {
			throw new IllegalArgumentException("Missing rdrand status causing exception!");
		}
		if (status == RdRandStatus.SUCCESS) {
			throw new IllegalArgumentException("Invalid status for an rdrand failure!");
		}
		this.status = status;
	}

	/**
	 * Returns the specific RdRand Status that resulted in this exception.
	 * @return The RdRand Status causing the exception.
	 */
	public RdRandStatus getStatus() {
		return status;
	}
}
