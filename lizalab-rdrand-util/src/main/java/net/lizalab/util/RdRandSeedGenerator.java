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

import org.uncommons.maths.random.SeedException;
import org.uncommons.maths.random.SeedGenerator;

/**
 * <code>org.uncommons.maths.random.SeeedGenerator</code> implementation 
 * that uses Intel's RDRAND instruction based Digital Random Number Generator 
 * (DRNG) to generate seed data.
 * 
 * Fast, hardware based cryptographically secure seeding option.
 *  
 * @author Hemant Padmanabhan
 * @since 1.1
 */
public final class RdRandSeedGenerator implements SeedGenerator {

	@Override
	public byte[] generateSeed(int length) throws SeedException {
		byte[] bytes = new byte[length];
		try {
			RdRandUtil.nextBytes(bytes);
		} catch (RdRandException e) {
			// catch and wrap RDRAND specific exception into the
			// exception specified by the interface.
			throw new SeedException(e.getStatus().getDesc());
		}
		return bytes;
	}

	@Override
	public String toString() {
		return "net.lizalab.util.RdRandUtil";
	}

}
