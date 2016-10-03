package us.hgk.caser.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Slurp {
	private static final Logger log = LoggerFactory.getLogger(Slurp.class);
	
	private static final int SLURP_BUFFER_SIZE_BYTES = 4096;
	private static final int SLURP_BUFFER_SIZE_CHARS = SLURP_BUFFER_SIZE_BYTES >> 1;

	/**
	 * Returns the concatenated contents of the given sequence of arrays. A
	 * source array may appear more than once in the sequence. Behavior is
	 * undefined if this method is used concurrently with modifications to the
	 * sequence or its elements.
	 * 
	 * @param arrays
	 *            A sequence of arrays to copy
	 * @return the concatenated sequence of elements within each element of
	 *         {@code arrays}
	 */
	public static char[] concatCharArrays(Iterable<? extends char[]> arrays) {
		ArrayList<char[]> listOfArrays = new ArrayList<>();
		int totalLength = 0;

		for (char[] array : arrays) {
			totalLength += array.length;
			listOfArrays.add(array);
		}

		listOfArrays.trimToSize();

		char[] result = new char[totalLength];
		int i = 0;

		for (char[] array : listOfArrays) {
			int thisLen = array.length;
			System.arraycopy(array, 0, result, i, thisLen);
			i += thisLen;
		}

		return result;
	}

	/**
	 * Returns the concatenated contents of the given sequence of arrays. A
	 * source array may appear more than once in the sequence. Behavior is
	 * undefined if this method is used concurrently with modifications to the
	 * sequence or its elements.
	 * 
	 * @param arrays
	 *            A sequence of arrays to copy
	 * @return the concatenated sequence of elements within each element of
	 *         {@code arrays}
	 */
	public static char[] concatCharArrays(char[][] arrays) {
		return concatCharArrays(Arrays.asList(arrays));
	}

	/**
	 * Returns the concatenated contents of the given sequence of arrays. A
	 * source array may appear more than once in the sequence. Behavior is
	 * undefined if this method is used concurrently with modifications to the
	 * sequence or its elements.
	 * 
	 * @param arrays
	 *            A sequence of arrays to copy
	 * @return the concatenated sequence of elements within each element of
	 *         {@code arrays}
	 */
	public static byte[] concatByteArrays(Iterable<? extends byte[]> arrays) {
		ArrayList<byte[]> listOfArrays = new ArrayList<>();
		int totalLength = 0;

		for (byte[] array : arrays) {
			totalLength += array.length;
			listOfArrays.add(array);
		}

		listOfArrays.trimToSize();

		byte[] result = new byte[totalLength];
		int i = 0;

		for (byte[] array : listOfArrays) {
			int thisLen = array.length;
			System.arraycopy(array, 0, result, i, thisLen);
			i += thisLen;
		}

		return result;
	}

	/**
	 * Returns the concatenated contents of the given sequence of arrays. A
	 * source array may appear more than once in the sequence. Behavior is
	 * undefined if this method is used concurrently with modifications to the
	 * sequence or its elements.
	 * 
	 * @param arrays
	 *            A sequence of arrays to copy
	 * @return the concatenated sequence of elements within each element of
	 *         {@code arrays}
	 */
	public static byte[] concatByteArrays(byte[][] arrays) {
		return concatByteArrays(Arrays.asList(arrays));
	}

	/**
	 * Reads the given reader to the end, returning the contents as a character
	 * array. This method does not close the reader.
	 * 
	 * @param reader
	 *            A reader to be read to the end
	 * @return The entire contents read from the reader
	 * @throws IOException
	 *             If an I/O error occurs while reading
	 */
	public static char[] slurpCharArray(Reader reader) throws IOException {
		ArrayList<char[]> buffers = new ArrayList<>();

		for (;;) {
			char[] buffer = new char[SLURP_BUFFER_SIZE_CHARS];
			int n = reader.read(buffer);
			if (n < 0) {
				break;
			} else if (n > 0) {
				buffers.add(n < buffer.length ? Arrays.copyOf(buffer, n) : buffer);
			}
		}

		return concatCharArrays(buffers);
	}

	/**
	 * Reads the given input stream to the end, returning the contents as a byte
	 * array. This method does not close the input stream.
	 * 
	 * @param stream
	 *            An input stream to be read to the end
	 * @return The entire contents read from the input stream
	 * @throws IOException
	 *             If an I/O error occurs while reading
	 */
	public static byte[] slurpByteArray(InputStream stream) throws IOException {
		ArrayList<byte[]> buffers = new ArrayList<>();

		for (;;) {
			byte[] buffer = new byte[SLURP_BUFFER_SIZE_BYTES];
			int n = stream.read(buffer);
			if (n < 0) {
				break;
			} else if (n > 0) {
				buffers.add(n < buffer.length ? Arrays.copyOf(buffer, n) : buffer);
			}
		}

		return concatByteArrays(buffers);
	}

	/**
	 * Reads the given reader to the end, returning the contents as a
	 * {@link String}. This method does not close the reader.
	 * 
	 * @param reader
	 *            A reader to be read to the end
	 * @return The entire contents read from the reader
	 * @throws IOException
	 *             If an I/O error occurs while reading
	 */
	public static String slurpString(Reader reader) throws IOException {
		return String.valueOf(slurpCharArray(reader));
	}

	/**
	 * Reads the given input stream, decoding according to the specified
	 * character set, returning the contents as a {@link String}. This method
	 * does not close the reader.
	 * 
	 * @param stream
	 *            An input stream to be read to the end
	 * @param charset
	 *            The character set by which to decode the input stream into
	 *            characters; if {@code null}, {@link Charset#defaultCharset()}
	 *            is used
	 * @return The entire contents read from the input stream
	 * @throws IOException
	 *             If an I/O error occurs while reading
	 */
	public static String slurpString(InputStream stream, Charset charset) throws IOException {
		if (charset == null) {
			charset = Charset.defaultCharset();
		}
		return slurpString(new InputStreamReader(stream, charset));
	}

	/**
	 * Reads the given input stream, decoding according to the default character
	 * set, returning the contents as a {@link String}.
	 * 
	 * @param stream
	 *            An input stream to be read to the end
	 * @return The entire contents read from the input stream
	 * @throws IOException
	 *             If an I/O error occurs while reading
	 */
	public static String slurpString(InputStream stream) throws IOException {
		return slurpString(new InputStreamReader(stream));
	}
}
