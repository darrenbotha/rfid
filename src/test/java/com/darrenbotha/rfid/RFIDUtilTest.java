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

import org.junit.Assert;
import org.junit.Test;

public class RFIDUtilTest {
	@Test
	public void testConvertCardIdentifier() {
		byte[] cardID = new byte[] { 1, 2, 3, 4 };
		String cardIdentifier = RFIDUtil.convertCardIdentifier(cardID);
		Assert.assertNotNull(cardIdentifier);
		Assert.assertTrue(!cardIdentifier.isEmpty());
		Assert.assertEquals("01020304", cardIdentifier);
	}
}
