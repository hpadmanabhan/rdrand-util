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

import java.util.Random;

/**
 * This class provides an extension to <code>java.util.Random</code> wrapping Intel's Digital
 * Random Number Generator (DRNG) known as Intel Secure Key, a name for its RDRAND instruction. 
 * Details available 
 * <a href="http://software.intel.com/en-us/articles/intel-digital-random-number-generator-drng-software-implementation-guide/">here</a>.
 * 
 * <p>As described there, Intel claims RDRAND is cryptographically strong, however,
 * you mileage on this may vary depending on the breadth of the tin foil hat you wear
 * since it is a closed source hardware based implementation on chip. It is being
 * integrated in the Linux Kernel as one of the sources for the entropy pool
 * and like any black box implementation being used in a cryptographically sensitive
 * environment, it should be tested regularly for randomness.</p>
 * 
 * <p>Since RDRAND cannot be seeded externally, no support is provided to specify a
 * seed. Refer to the documentation linked above on seeding details.</p>
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public final class RdRandRandom extends Random {

	private static final long serialVersionUID = -2909432877994903004L;
	
	/**
	 * Constructs a random number generator (RNG) wrapping the
	 * Intel RDRAND Instruction.
	 * Invokes the super class constructor in a manner that results
	 * in its own implementation of <code>setSeed</code> being invoked.
	 * Verifies that the JNI shared library accessing RDRAND is loaded
	 * and running successfully.
	 * 
	 * @throws RdRandException If RdRand returns a non-success status or the native library is not loaded.
	 */
	public RdRandRandom() {
		// Make super class constructor invoke our implementation of setSeed.
		super(0);
		// Verify RdRand availability.
		RdRandStatus status = RdRandUtil.verify();
		if (status != RdRandStatus.SUCCESS) {
			throw new RdRandException(status);
		}
	}

	/**
	 * Generates user specified number of random bytes.
	 * 
	 * @param bytes The array to be filled with random bytes.
	 * @throws RdRandException If RdRand returns a non-success status or the native library is not loaded.
	 */
	@Override
	public void nextBytes(byte[] bytes) {
		RdRandUtil.nextBytes(bytes);
	}

	/**
	 * Overridden empty implementation. RDRAND cannot be seeded externally. 
	 * Refer to the Intel documentation linked above for details on their
	 * RNG implementation.
	 */
	@Override
	public synchronized void setSeed(long seed) {
	}

	/**
	 * Generates an integer containing the user-specified number of
     * pseudo-random bits (right justified, with leading zeros).  This
     * method overrides a <code>java.util.Random</code> method, and serves
     * to provide a source of random bits to all of the methods inherited
     * from that class (for example, <code>nextInt</code>,
     * <code>nextLong</code>, and <code>nextFloat</code>). This is a mirror
     * implementation of the same method in <code>java.security.SecureRandom</code>.
     *
     * @param bits number of pseudo-random bits to be generated, where
     * 0 <= <code>bits</code> <= 32.
     *
     * @return an <code>int</code> containing the user-specified number
     * of pseudo-random bits (right justified, with leading zeros).
	 */
	@Override
	protected int next(int bits) {
		int numBytes = (bits+7)/8;
		int next = RdRandUtil.next(numBytes);
        return next >>> (numBytes*8 - bits);
	}

}
