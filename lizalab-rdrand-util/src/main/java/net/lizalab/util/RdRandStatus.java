package net.lizalab.util;

/**
 * Enumeration of possible statuses returned by the RdRand
 * library API calls. Used in verifying the availability of
 * RdRand on a given platform.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public enum RdRandStatus {

	SUCCESS(1, "The rdrand call was successful, the hardware was ready, and a random number was returned."),
	NOT_READY(-1, "The rdrand call was unsuccessful, the hardware was not ready, and a random number was not returned."),
	SUPPORTED(-2, "The rdrand instruction is supported by the host hardware."),
	UNSUPPORTED(-3, "The rdrand instruction is unsupported by the host hardware."),
	UNKNOWN(-4, "Whether or not the hardware supports the rdrand instruction is unknown.");
	
	/**
	 * Numeric code for RdRand status. API calls return these values.
	 */
	private final int code;
	
	/**
	 * Text description for RdRand status.
	 */
	private final String desc;

	/**
	 * Returns the numeric code for RdRand status. This is the value
	 * returned by the RdRand library API.
	 * @return The RdRand status code.
	 */
	public final int getCode() {
		return code;
	}

	/**
	 * Returns text description for the RdRand status.
	 * @return String describing the RdRand status.
	 */
	public final String getDesc() {
		return desc;
	}

	/**
	 * Default constructor, initializes enum constants with their
	 * corresponding code and description.
	 * @param code The numeric code for the status returned by RdRand.
	 * @param desc String describing the RdRand status.
	 */
	private RdRandStatus(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	/**
	 * Returns RdRandStatus corresponding to the specified numeric code.
	 * @param code The numeric code for which corresponding RdRandStatus is required.
	 * @return The RdRandStatus matching the specified numeric status code.
	 */
	public static final RdRandStatus getStatusByCode(int code) {
		for (RdRandStatus status : RdRandStatus.values()) {
			if (status.getCode() == code) {
				return status;
			}
		}
		return null;
	}
}
