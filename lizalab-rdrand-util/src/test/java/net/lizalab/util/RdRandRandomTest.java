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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
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
	
	private int[] ndigits;
	
	private int values = 10000000;
	
	private double mean;
	
	private double stdDev;
	
	private double var;
	
	private double smLowerRng3SD;
	
	private double smUpperRng3SD;
	
	private double smLowerRng2SD;
	
	private double smUpperRng2SD;
	
	private int numPoints = 1000000;
	
	private int precision;
	
	private BigDecimal pi = new BigDecimal(Math.PI);;
	
	/**
	 * Verifies Generation of random bytes and their population
	 * in the specified array.
	 */
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
	 * Runs the mean test for the specified instance of Random.
	 * @param random The RNG instance to test.
	 * @param doAssert Flag to indicate whether result should be asserted or simply logged.
	 */
	private void meanTest(Random random, boolean doAssert) {
		final String methodName = "meanTest : ";
		
		ndigits = new int[10];
		// Initialize the array
		for (int i = 0; i < 10; i++) {
			ndigits[i] = 0;
		}
		double x;
		int n;
		
		SummaryStatistics stats = new SummaryStatistics();
	
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
		double meanDiff = (sampleMean - mean)/mean;
		LOGGER.info("{} mean: {}, diff: {}", methodName, sampleMean, meanDiff);
		LOGGER.info("{} sd: {}", methodName, stats.getStandardDeviation());
		LOGGER.info("{} var: {}", methodName, sampleVar);
		// Verify results.
		boolean varResult = sampleVar < var;
		String varMsg = "Random Variance exceeds max expectation!";
		boolean ci99Result = sampleMean >= smLowerRng3SD
				&& sampleMean <= smUpperRng3SD;
		String ci99Msg = "Random 99.7% CI test failed!";
		boolean ci95Result = sampleMean >= smLowerRng2SD
				&& sampleMean <= smUpperRng2SD;
		String ci95Msg = "Random 95% CI test failed!";
		if (doAssert) {
			assertTrue(varMsg, varResult);
			assertTrue(ci99Msg, ci99Result);
			assertTrue(ci95Msg, ci95Result);
		}
		else {
			if (!varResult) {
				LOGGER.warn("{} {}", methodName, varMsg);
			}
			if (!ci99Result) {
				LOGGER.warn("{} {}", methodName, ci99Msg);
			}
			if (!ci95Result) {
				LOGGER.warn("{} {}", methodName, ci95Msg);
			}
		}
	}

	/**
	 * Tests RdRandRandom by verifying the average of the distribution of digits 0-9
	 * over 100 million values. Also runs the test for Random and SecureRandom for
	 * reference.
	 * Based on Mean Test outlined in <i>Beautiful Testing</i> published by O'Reilly.
	 */
	@Test
	public final void testRdRandRandomMean() {
		final String methodName = "testRdRandRandom : ";
		
		SummaryStatistics stats = new SummaryStatistics();
		// Initialize the array
		ndigits = new int[10];
		for (int i = 0; i < 10; i++) {
			ndigits[i] = 0;
			stats.addValue(i);
		}
				
		// Calculate the confidence intervals to assert.
		mean = stats.getMean();
		stdDev = stats.getStandardDeviation();
		var = stats.getVariance();
		LOGGER.info("{} Normal mean: {}", methodName, mean);
		LOGGER.info("{} Normal std: {}", methodName, stdDev);
		LOGGER.info("{} Normal var: {}", methodName, var);
		// 99.7% CI is within 3 std.
		double expectedDev3SD = 3 * stdDev / Math.sqrt(values);
		smLowerRng3SD = mean - expectedDev3SD;
		smUpperRng3SD = mean + expectedDev3SD;
		// 95% CI is within 2 std.
		double expectedDev2SD = 2 * stdDev / Math.sqrt(values);
		smLowerRng2SD = mean - expectedDev2SD;
		smUpperRng2SD = mean + expectedDev2SD;
		LOGGER.info("{} Generating {} values.", methodName, values);
		LOGGER.info(
				"{} Sample mean expected in range {} - {} 99.7% of the times.",
				methodName, smLowerRng3SD, smUpperRng3SD);
		LOGGER.info(
				"{} Sample mean expected in range {} - {} 95% of the times.",
				methodName, smLowerRng2SD, smUpperRng2SD);
		
		LOGGER.info("{} Running for Random..", methodName);
		Random random = new Random();
		meanTest(random, false);

		LOGGER.info("{} Running for RdRand..", methodName);
		random = new RdRandRandom();
		meanTest(random, true);
		
		LOGGER.info("{} Running for SecureRandom..", methodName);
		random = new SecureRandom();
		meanTest(random, false);
	}
	
	/**
	 * Calculates the expected precision for the Pi approximation
	 * based on the number of points being generated. Generally,
	 * for every 100X increase in points a 10X increase in precision
	 * should be observed.
	 * @param points Count of points being generated for the test.
	 * @return Number of digits of precision expected in the Pi approximation.
	 */
	private int expectedPrecision(int points) {
		int d = points;
		int f = 0;
		while (d >= 100) {
			d = d / 100;
			f++;
		}
		return f; 
	}
	
	/**
	 * Runs the Monte Carlo Pi approximation test for the specified
	 * instance of Random.
	 * @param random The RNG instance to test.
	 * @param doAssert Flag to indicate whether result should be asserted or simply logged.
	 */
	private void monteCarloPiTest(Random random, boolean doAssert) {
		final String methodName = "monteCarloPiTest : ";
		
		int inRandCircle = 0;
		
		// xr and yr will be the random point
		// zr will be the calculated distance to the center
		double xr, yr, zr;
		
		long start = System.currentTimeMillis();
		for(int i=0; i < numPoints; i++) {
			xr = random.nextDouble();
			yr = random.nextDouble();
			
			zr = (xr * xr) + (yr * yr);
			if (zr <= 1.0) {
				inRandCircle++;
			}
		}
		long end = System.currentTimeMillis();
		LOGGER.info("{} Time: {}ms", methodName, end - start);
		
		// calculate the Pi approximations
		double randomPi = (double)inRandCircle / numPoints * 4.0;
		
		// calculate the difference and % error
		double diff = (randomPi - Math.PI);
		double randomError = diff/Math.PI * 100;
		LOGGER.info("{} Pi Approximation: {}, Diff: {}, Error %: {}", methodName, randomPi, diff, randomError);
		BigDecimal randomPiBD = new BigDecimal(randomPi);
		randomPiBD = randomPiBD.setScale(precision - 1,  RoundingMode.DOWN);
		// Verify result.
		boolean result = randomPiBD.compareTo(pi) == 0;
		String msg = "Pi approximation not sufficiently precise for " + random.getClass();
		if (doAssert) {
			assertTrue(msg, result);
		}
		else {
			if (!result) {
				LOGGER.warn("{} {}", methodName, msg);
			}
		}
	}
	
	/**
	 * Tests RdRandRandom using the Monte Carlo Pi approximation test.
	 * Also runs the test for Random and SecureRandom for reference.
	 */
	@Test
	public final void testRdRandRandomMonteCarloPi() {
		final String methodName = "testRdRandRandomMonteCarloPiTest : ";
		
		precision = expectedPrecision(numPoints);
		// Since the first digit for pi is before the decimal point, we adjust the scale accordingly.
		pi = pi.setScale(precision - 1, RoundingMode.DOWN);
		LOGGER.info("{} Generating {} points for Monte Carlo Pi Test. Expected precision: {} digits, {}", 
				methodName, numPoints, precision, pi.toPlainString());
		
		LOGGER.info("{} Running for Random..", methodName);
		Random random = new Random();
		monteCarloPiTest(random, false);
		
		LOGGER.info("{} Running for RdRand..", methodName);
		random = new RdRandRandom();
		monteCarloPiTest(random, true);
		
		LOGGER.info("{} Running for SecureRandom..", methodName);
		random = new SecureRandom();
		monteCarloPiTest(random, false);
	}
}
