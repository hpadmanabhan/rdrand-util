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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JNI Wrapper to the RdRand library exposing available functionality
 * useful in the Java context as native functions.
 * 
 * @author Hemant Padmanabhan
 * @since 1.0
 */
public final class RdRandUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RdRandUtil.class);
	
	/**
	 * Name of the shared library implementing the native methods.
	 */
	public static final String LIB_NAME = "librdrandjutil.so";
	
	/**
	 * Flag indicating whether the native shared library is loaded.
	 */
	private static final boolean LOADED;
	
	/**
	 * Bytes to read at a time from an input stream.
	 */
	private static final int READ_BYTES = 1024;
	
	/**
	 * Loads the shared library implementing the native methods.
	 */
	static {
		LOADED = loadNativeLibrary();
	}
	
	/**
	 * Hidden constructor for utility class.
	 */
	private RdRandUtil() {
	}
	
	/**
	 * Loads the shared library implementing the native methods. Expects the
	 * library to be bundled with this class in the same archive at the
	 * archive root. The library is extracted from the archive and loaded to
	 * the temp directory indicated by the system property java.io.tmpdir.
	 * Any previously loaded library will always be overwritten.
	 * @return True if the native library was etracted and loaded successfully.
	 */
	private static synchronized boolean loadNativeLibrary() {
		final String methodName = "loadNativeLibrary : ";
		
		if (LOADED) {
            return LOADED;
        }
		// Check that the shared library is packaged.
		InputStream reader = RdRandUtil.class.getResourceAsStream(File.separator + LIB_NAME);
		if (reader == null) {
			return false;
		}
		// Extract the native shared library from jar to temp dir.
		String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
		/*
		 *  Since this library will run in user land we will prefix the user to the
		 *  extracted library to ensure unique instance of the shared object writable by
		 *  the user avoiding errors trying to overwrite library written previously by a different user.
		 */
		File extractedLibFile = new File(tempFolder, System.getProperty("user.name") + "_" + LIB_NAME);
		LOGGER.debug("{} Loading bundled native shared library to {}", methodName, extractedLibFile.getAbsolutePath());
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(extractedLibFile);
			byte[] buffer = new byte[READ_BYTES];
			int bytesRead = 0;
			while ((bytesRead = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			LOGGER.error("{} Failed to extract and load bundled native shared library due to {}", methodName, e);
			return false;
		} finally {
			// Check and close streams.
			if (writer != null) {
				try { writer.close(); } catch (Exception e){}
			}
			try { reader.close(); } catch (Exception e){}
		}
		// Load the extracted library.
		try {
			System.load(extractedLibFile.getAbsolutePath());
		} catch (UnsatisfiedLinkError e) {
			LOGGER.error("{} Failed to load extracted native library due to {}", methodName, e);
			return false;
		}
		LOGGER.debug("{} Native shared library loaded.", methodName);
		return true;
	}

	/**
	 * Native method to verify RdRand status. Invokes the RdRand instruction
	 * and propagates the numeric status returned.
	 * @return The numeric status code returned by RdRand instruction.
	 */
	private static native int verifyNative();
	
	/**
	 * Verifies the the availability and status of the RdRand instruction
	 * on the host.
	 * @return Status of the RdRand instruction on the host.
	 */
	public static RdRandStatus verify() {
		if (!LOADED) {
			return RdRandStatus.NOT_LOADED;
		}
		int result = verifyNative();
		return RdRandStatus.getStatusByCode(result);
	}
	
	/**
	 * Native method fetching the specified number of bytes in the provided
	 * byte array from RdRand.
	 * @param bytes The byte array to fill with random bytes.
	 * @param size The number of random bytes to fetch.
	 * @return Numeric status code returned by RdRand for the fetch operation.
	 */
	private static native int nextBytesNative(byte[] bytes, int size);
	
	/**
	 * Fetches random bytes from RdRand and places them into the user specified
	 * array. The number of random bytes fetched is equal to the length of the
	 * byte array.
	 * @param bytes The byte array to fill with random bytes.
	 * @throws RdRandException If RdRand returns a non-success status or the native library is not loaded.
	 */
	public static void nextBytes(byte[] bytes) {
		if (!LOADED) {
			throw new RdRandException(RdRandStatus.NOT_LOADED);
		}
		int result = nextBytesNative(bytes, bytes.length);
		RdRandStatus status = RdRandStatus.getStatusByCode(result);
		if (status != RdRandStatus.SUCCESS) {
			throw new RdRandException(status);
		}
	}
}
