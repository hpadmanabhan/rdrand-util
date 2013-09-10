package net.lizalab.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

/**
 * Unit tests for the RdRandUtil class.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public class RdRandUtilTest {

	@Test
	public final void testNextBytes() {
		// Verify library is loaded and available
		RdRandStatus status = RdRandUtil.verify();
		// Prepare array to receive random bytes
		byte[] bytes = new byte[32];
		// Proceed depending on the verify result.
		if (status == RdRandStatus.SUCCESS) {
			// Library successfully loaded
			RdRandUtil.nextBytes(bytes);
			byte[] emptyBytes = new byte[32];
			// Verify the library filled the provided array with random byte values.
			assertFalse(Arrays.equals(bytes, emptyBytes));
		}
		else {
			// Verify failed, nextBytes should fail with the same status.
			RdRandException error = null;
			try {
				RdRandUtil.nextBytes(bytes);
			} catch (RdRandException e) {
				error = e;
			}
			assertNotNull(error);
			assertEquals("Different status returned by nextBytes vs verify..", status, error.getStatus());
		}
	}

}
