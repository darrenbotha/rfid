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

package com.darrenbotha.rfid.hidglobal;

import java.util.List;

import javax.smartcardio.CardTerminal;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.darrenbotha.rfid.RFIDReader;

public class HIDGlobalReaderTest {
	private static final String READER_NAME = "OMNIKEY CardMan 5x21-CL 0";
	
	RFIDReader reader;
	
	@Before
	public void setUp() throws Exception {
		reader = new HIDGlobalReader(READER_NAME);
		Assert.assertNotNull(reader);
		Assert.assertTrue(reader instanceof HIDGlobalReader);
		Assert.assertTrue(reader.connect());
	}
	
	@After
	public void teardown() throws Exception {
		reader.disconnect();
	}	
	
	@Test
	public void testListAvailableReaders() throws Exception {
		List<CardTerminal> list = reader.listAvailableReaders();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
	}
	
	@Test
	public void testAuthenticate() throws Exception {		
		Assert.assertTrue(reader.authenticate(0));	
	}
	
	@Test
	public void testGetCardIdentifier() throws Exception {
		byte[] cardID = reader.getCardIdentifier();
		Assert.assertNotNull(cardID);
		Assert.assertEquals(4, cardID.length);	
	}
	
	@Test
	public void testRead() throws Exception {	
		reader.authenticate(0);
		byte[] block = reader.read(0);
		Assert.assertNotNull(block);
		Assert.assertTrue(block.length > 0);
	}
	
	@Test
	public void testWrite() throws Exception {
		reader.authenticate(8);
		byte[] block = reader.read(8);
		Assert.assertTrue(reader.write(8, block));
		byte[] newBlock = reader.read(8);
		
		for (int i = 0; i < block.length; i++) {
			Assert.assertEquals(block[i], newBlock[i]);
		}
	}
	
	@Test
	public void testGetTotalBlocks() throws Exception {
		reader.authenticate(0);
		Assert.assertTrue(reader.getTotalBlocks() > 0);
	}
}
