package net.lizalab.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for the RdRandException object.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public class RdRandExceptionTest {

	/**
	 * Instance of class under test.
	 */
	private RdRandException rdRandException;
	
	/**
	 * Test status.
	 */
	private RdRandStatus testStatus = RdRandStatus.UNSUPPORTED;
	
	/**
	 * Tests instantiation and accessor for status.
	 */
	@Test
	public final void testGetStatus() {
		rdRandException = new RdRandException(testStatus);
		
		assertTrue(testStatus == rdRandException.getStatus());
	}

	/**
	 * Verifies RdRandException cannot be instantiated with a null status.
	 */
	@Test(expected=IllegalArgumentException.class)
	public final void testRdRandExceptionNullStatus() {
		rdRandException = new RdRandException(null);
	}
	
	/**
	 * Verifies RdRandException cannot be instantiated with a success status.
	 */
	@Test(expected=IllegalArgumentException.class)
	public final void testRdRandExceptionSuccessStatus() {
		rdRandException = new RdRandException(RdRandStatus.SUCCESS);
	}
	
}
