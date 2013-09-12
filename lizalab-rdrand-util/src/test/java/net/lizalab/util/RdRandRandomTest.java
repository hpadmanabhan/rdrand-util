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

package net.lizalab.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
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
	 * Tests RdRandRandom by verifying the average of the distribution of digits 0-9
	 * over 100 million values.
	 * Based on Mean Test outlined in <i>Beautiful Testing</i> published by O'Reilly.
	 */
	@Test
	public final void testRdRandRandomMeanTest() {
		final String methodName = "testRdRandRandom : ";

		int[] ndigits = new int[10];
		double x;
		int n;
		int values = 100000000;
		// Calculate the confidence intervals to assert.
		double mean = 4.5;
		double stdDev = 3.0276503541;
		double var = 9.166666667;
		// 99.7% CI is within 3 std.
		double expectedDev3SD = 3 * stdDev / Math.sqrt(values);
		double smLowerRng3SD = mean - expectedDev3SD;
		double smUpperRng3SD = mean + expectedDev3SD;
		// 95% CI is within 2 std.
		double expectedDev2SD = 2 * stdDev / Math.sqrt(values);
		double smLowerRng2SD = mean - expectedDev2SD;
		double smUpperRng2SD = mean + expectedDev2SD;
		LOGGER.info("{} Generating {} values.", methodName, values);
		LOGGER.info(
				"{} Sample mean expected in range {} - {} 99.7% of the times.",
				methodName, smLowerRng3SD, smUpperRng3SD);
		LOGGER.info(
				"{} Sample mean expected in range {} - {} 95% of the times.",
				methodName, smLowerRng2SD, smUpperRng2SD);

		SummaryStatistics stats = new SummaryStatistics();
		Random random = new RdRandRandom();

		// Initialize the array
		for (int i = 0; i < 10; i++) {
			ndigits[i] = 0;
		}

		long start = System.currentTimeMillis();
		// Test the random number generator a whole lot
		for (long i = 0; i < values; i++) {
			// generate a new random number between 0 and 9
			x = random.nextDouble() * 10.0;
			n = (int) x;
			stats.addValue(n);
			// count the digits in the random number
			ndigits[n]++;
		}
		long end = System.currentTimeMillis();

		LOGGER.info("{} Time: {}ms", methodName, end - start);
		LOGGER.info("{} Distribution:", methodName);
		// Print the results
		for (int i = 0; i < 10; i++) {
			LOGGER.info("{} {}: {}", methodName, i, ndigits[i]);
		}
		double sampleMean = stats.getMean();
		double sampleVar = stats.getVariance();
		LOGGER.info("{} mean: {}", methodName, sampleMean);
		LOGGER.info("{} sd: {}", methodName, stats.getStandardDeviation());
		LOGGER.info("{} var: {}", methodName, sampleVar);
		// Run assertions
		assertTrue("Variance exceeds max expectation of 9!", sampleVar < var);
		assertTrue("99.7% CI test failed!", sampleMean >= smLowerRng3SD
				&& sampleMean <= smUpperRng3SD);
		assertTrue("95% CI test failed!", sampleMean >= smLowerRng2SD
				&& sampleMean <= smUpperRng2SD);
	}

}
