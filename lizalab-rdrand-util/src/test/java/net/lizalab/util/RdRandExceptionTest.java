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
