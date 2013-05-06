/* Copyright 2013 Yamana Laboratory, Waseda University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.queuelinker.system.util;

public class ByteConverter {
	public static byte[] getByteArrayOfInt(int val) {
		byte[] ret = new byte[4];
		ret[0] = (byte)(val >>> 24);
		ret[1] = (byte)(val >>> 16);
		ret[2] = (byte)(val >>>  8);
		ret[3] = (byte)val;
		return ret;
	}

	public static void assignShortValue(byte[] array, int offset, short val) {
		array[offset] = (byte)(val >>> 8);
		array[offset+1] = (byte)val;
	}
	
	public static short getShortValue(byte[] array, int offset) {
		return (short)((array[offset] & 0xFF) << 8 | array[offset+1] & 0xFF); 
	}
	
	public static short getShortValue(byte[] array) {
		return (short)((array[0] & 0xFF) << 8 | array[1] & 0xFF); 
	}
	
	public static void assignIntValue(byte[] array, int offset, int val) {
		array[offset]   = (byte)(val >>> 24);
		array[offset+1] = (byte)(val >>> 16);
		array[offset+2] = (byte)(val >>>  8);
		array[offset+3] = (byte)val;
	}
	
	public static int getIntValue(byte[] array) {
		return (array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16 | 
				(array[2] & 0xFF) << 8 | array[3] & 0xFF;
	}
	
	public static int getIntValue(byte[] array, int offset) {
		return (array[offset] & 0xFF) << 24 | (array[offset+1] & 0xFF) << 16 | 
				(array[offset+2] & 0xFF) << 8 | array[offset+3] & 0xFF;
	}
	
	public static void assignLongValue(byte[] array, int offset, long val) {
		array[offset]   = (byte)(val >>> 56);
		array[offset+1] = (byte)(val >>> 48);
		array[offset+2] = (byte)(val >>> 40);
		array[offset+3] = (byte)(val >>> 32);
	
		array[offset+4] = (byte)(val >>> 24);
		array[offset+5] = (byte)(val >>> 16);
		array[offset+6] = (byte)(val >>>  8);
		array[offset+7] = (byte) val;
	}
	
	public static long getLongValue(byte[] array, int offset) {
		return (array[offset] & 0xFFL) << 56 | (array[offset+1] & 0xFFL) << 48 |
		(array[offset+2] & 0xFFL) << 40 | (array[offset+3] & 0xFFL) << 32 |
		(array[offset+4] & 0xFFL) << 24 | (array[offset+5] & 0xFFL) << 16 |
		(array[offset+6] & 0xFFL) << 8 | (array[offset+7] & 0xFFL);
	}
}
