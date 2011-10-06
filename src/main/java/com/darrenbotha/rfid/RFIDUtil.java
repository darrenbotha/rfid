/**
* Copyright 2011 Darren Botha.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.darrenbotha.rfid;

/**
 * Utility class supporting the RFID reader.
 * 
 * @author Darren Botha
 */
public class RFIDUtil {
	/**
	 * Convert method of byte array to hexadecimal string.
	 * 
	 * @param cardID The byte array of the card identifier (length of 4).
	 * @return The hexadecimal card identifier, otherwise NULL.
	 */
	public static String convertCardIdentifier(final byte[] cardID) {
		if (cardID == null || cardID.length != 4) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (byte b : cardID) {
			builder.append(String.format("%02X", b));
		}
		
		return builder.toString();
	}
}
