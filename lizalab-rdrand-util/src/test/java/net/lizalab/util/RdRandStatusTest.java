package net.lizalab.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the RdRandStatus enumeration.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
@RunWith(value=Parameterized.class)
public class RdRandStatusTest {

	/**
	 * Enumeration constant under test.
	 */
	private RdRandStatus status;
	
	/**
	 * Expected numeric code for the enum constant under test.
	 */
	private int expectedCode;
	
	/**
	 * Expected text description for the enum constant under test.
	 */
	private String expectedDesc;
	
	/**
	 * Collection of statuses to test and the expected numeric
	 * codes for them and their text descriptions.
	 * @return Collection of test parameters.
	 */
	@Parameters
	public static Collection<String[]> getTestParameters() {
		return Arrays.asList(new String[][] {
				{"SUCCESS","1", "The rdrand call was successful, the hardware was ready, and a random number was returned."},
				{"NOT_READY","-1", "The rdrand call was unsuccessful, the hardware was not ready, and a random number was not returned."},
				{"SUPPORTED","-2", "The rdrand instruction is supported by the host hardware."},
				{"UNSUPPORTED","-3", "The rdrand instruction is unsupported by the host hardware."},
				{"UNKNOWN","-4", "Whether or not the hardware supports the rdrand instruction is unknown."},
				{"NOT_LOADED","-5","The RdRand Java Utility Native Shared Library is not loaded."}
		});
	}
	
	/**
	 * Constructor for initializing the test with the test parameters.
	 * @param statusStr String representing the enum constant for the RdRandStatus being tested.
	 * @param expectedCode String representing the numeric code expected for the RdRandStatus being tested.
	 * @param expectedDesc String containing the text description for the RdRandStatus being tested.
	 */
	public RdRandStatusTest(String statusStr, String expectedCode, String expectedDesc) {
		this.status = RdRandStatus.valueOf(statusStr);
		this.expectedCode = Integer.parseInt(expectedCode);
		this.expectedDesc = expectedDesc;
	}
	
	/**
	 * Verifies numeric code for RdRandStatus enum constants.
	 */
	@Test
	public final void testGetCode() {
		assertEquals("Incorrect code returned for " + status, expectedCode, status.getCode());
	}

	/**
	 * Verifies description for RdRandStatus enum constants.
	 */
	@Test
	public final void testGetDesc() {
		assertEquals("Incorrect description returned for " + status, expectedDesc, status.getDesc());
	}

	@Test
	public final void testGetStatusByCode() {
		assertEquals("Incorrect status returned by code based lookup for " + status, 
				status, RdRandStatus.getStatusByCode(status.getCode()));
	}

}
