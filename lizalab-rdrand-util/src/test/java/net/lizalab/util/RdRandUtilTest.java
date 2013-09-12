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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for the RdRandUtil class.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public class RdRandUtilTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RdRandUtilTest.class);
	
	@Test
	public final void testNextBytes() {
		final String methodName = "testNextBytes : ";
		
		// Verify library is loaded and available
		RdRandStatus status = RdRandUtil.verify();
		// Prepare array to receive random bytes
		byte[] bytes = new byte[32];
		// Proceed depending on the verify result.
		if (status == RdRandStatus.SUCCESS) {
			LOGGER.debug("{} Native Shared Library loaded successfully, running success branch tests.", methodName);
			// Library successfully loaded
			RdRandUtil.nextBytes(bytes);
			byte[] emptyBytes = new byte[32];
			// Verify the library filled the provided array with random byte values.
			assertFalse(Arrays.equals(bytes, emptyBytes));
		}
		else {
			// Verify failed, nextBytes should fail with the same status.
			LOGGER.debug("{} Native Shared Library load failed with {}, running failure branch tests.", 
					methodName, status);
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
