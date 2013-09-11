package net.lizalab.util;

import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Randomness test for RdRandRandom that also verifies
 * basic operation.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public class RdRandRandomTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RdRandRandomTest.class);
	
	@Test
	public final void testNextBytes() {
		// Prepare array to receive random bytes
		byte[] bytes = new byte[32];
		Random random = new RdRandRandom();
		random.nextBytes(bytes);
		byte[] emptyBytes = new byte[32];
		// Verify the library filled the provided array with random byte values.
		assertFalse(Arrays.equals(bytes, emptyBytes));
	}

	/**
	 * Tests RdRandRandom by verifying the distribution of digits 0-9
	 * over 10 million iterations.
	 */
	@Test
	public final void testRdRandRandomMeanTest() {
		final String methodName = "testRdRandRandom : ";
		
		int[] ndigits = new int[10];
	    double x;
	    int n;
	    
	    Random random = new RdRandRandom();
	    
	    // Initialize the array 
	    for (int i = 0; i < 10; i++) {
	      ndigits[i] = 0;
	    }
	    
	    long start = System.currentTimeMillis();
	    // Test the random number generator a whole lot
	    for (long i=0; i < 10000000; i++) {
	      // generate a new random number between 0 and 9
	      x = random.nextDouble() * 10.0;
	      n = (int) x;
	      //count the digits in the random number
	      ndigits[n]++;
	    }
	    long end = System.currentTimeMillis();
	    LOGGER.info("{} Time: {}ms", methodName, end - start);
	    LOGGER.info("{} Results:", methodName);
	    // Print the results
	    for (int i = 0; i < 10; i++) {
	    	LOGGER.info("{} {}: {}", methodName, i, ndigits[i]);
	    }
	}

}
