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

import java.nio.ByteBuffer;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import com.darrenbotha.rfid.RFIDException;
import com.darrenbotha.rfid.RFIDReader;

/**
 * An implementation of the RFIDReader interface for the 
 * HID Global family of RFID readers.
 * 
 * @author Darren Botha
 */
public class HIDGlobalReader implements RFIDReader {
	// The response buffer size.
	private static final int RESPONSE_SIZE = 258;
	// The number of blocks on a MIFARE 1K card.
	private static final int BLOCKS_MIFARE_1K = 64;
	// The number of blocks on a MIFARE 16K card.
	private static final int BLOCKS_MIFARE_4K = 255;
	// If the number of blocks could not be determined.
	private static final int BLOCKS_UNSUPPORTED = -1;
	
	String readerName;	
	TerminalFactory terminalFactory;
	CardTerminal cardTerminal;
	Card card;
	CardChannel cardChannel;
	
	/**
	 * Constructor for HIDGlobalReader object.
	 * 
	 * @param readerName The name of the RFID reader to use.
	 */
	public HIDGlobalReader(final String readerName) {
		this.readerName = readerName;
		this.terminalFactory = TerminalFactory.getDefault();
	}
	
	/**
	 * Connect to the RFID reader.
	 * 
	 * @return Whether or not the connection was successful.
	 * @throws RFIDException - if there is no reader present.
	 */
	@Override
	public boolean connect() throws RFIDException {
		cardTerminal = terminalFactory.terminals().getTerminal(readerName);
		
		if (cardTerminal == null) {
			throw new RFIDException("Unable to find terminal.");
		}
		
		try {
			if (!cardTerminal.isCardPresent()) {
				return false;
			}
			
			cardTerminal.waitForCardPresent(0);
			card = cardTerminal.connect("*");
			cardChannel = card.getBasicChannel();
		} catch (CardException e) {
			throw new RFIDException(e.getMessage());
		}
		
		return true;
	}

	/**
	 * Disconnect from an active connected RFID reader.
	 * 
	 * @throws RFIDException - if the disconnection failed.
	 */
	@Override
	public void disconnect() throws RFIDException {
		try {
			if (card != null) {
				card.disconnect(true);
			}
		} catch (CardException e) {
			throw new RFIDException(e.getMessage());
		} finally {
			cardChannel = null;
			card = null;
			cardTerminal = null;
		}
	}

	/**
	 * List all the available RFID readers on the system.
	 * 
	 * @return List containing all the RFID readers.
	 * @throws RFIDException - if an error occurred finding the RFID readers.
	 */
	@Override
	public List<CardTerminal> listAvailableReaders() throws RFIDException {
		try {
			return terminalFactory.terminals().list();
		} catch (CardException e) {
			throw new RFIDException(e.getMessage());
		}
	}

	/**
	 * Authenticate to a block on the RFID card on the reader.
	 * 
	 * @param blockNo The block number to authenticate to.
	 * @return Whether or not the authentication was successful.
	 * @throws RFIDException - if an error occurred authenticating.
	 */
	@Override
	public boolean authenticate(int blockNo) throws RFIDException {
		byte[] command = new byte[] {
			(byte)0xFF, 
			(byte)0x82, 
			(byte)0x20,
			(byte)0x1A, 
			(byte)0x06, 
			(byte)0xFF,
			(byte)0xFF, 
			(byte)0xFF, 
			(byte)0xFF,
			(byte)0xFF, 
			(byte)0xFF
		};
		
		byte[] response = send(command);
		
		if (!isValidResponse(response)) {
			return false;
		}
		
		command = new byte[] {
			(byte)0xFF, 
			(byte)0x88, 
			(byte)0x00, 
			(byte)blockNo, 
			(byte)0x60, 
			(byte)0x1A
		};
		
		response = send(command);
		
		if (!isValidResponse(response)) {
			return false;
		}
		
		return true;
	}

	/**
	 * Get the unique identifier of the RFID card on the reader.
	 * 
	 * @return The 4 byte array of the unique identifier.
	 * @throws RFIDException - if an error occurred reading the card identifier.
	 */
	@Override
	public byte[] getCardIdentifier() throws RFIDException {
		byte[] command = new byte[] {
			(byte)0xFF, 
			(byte)0xCA, 
			(byte)0x00,
			(byte)0x00, 
			(byte)0x00
		};
		
		byte[] response = send(command);

		if (!isValidResponse(response) || response.length < 6) {
			return null;
		}
				
		return new byte[] {
			response[3], response[2],
			response[1], response[0]
		};
	}
	
	/**
	 * Read the all the data on a single block on the RFID card.
	 * 
	 * @param blockNo The block number to read.
	 * @return The byte array containing the data.
	 * @throws RFIDException - if an error occurred reading the block.
	 */
	@Override
	public byte[] read(int blockNo) throws RFIDException {
		byte[] command = new byte[] {
			(byte)0xFF, 
			(byte)0xB0, 
			(byte)0x00,
			(byte)blockNo, 
			(byte)0x10		
		};
		
		byte[] response = send(command);
		
		if (!isValidResponse(response)) {
			return null;
		}
		
		return stripResponse(response);
	}

	/**
	 * Write data to a single block on the RFID card.
	 * 
	 * @param blockNo The block number to write.
	 * @param data The data to write to the block.
	 * @return Whether or not the write was successful.
	 * @throws RFIDException - if an error occurred writing to the block.
	 */
	@Override
	public boolean write(int blockNo, byte[] data) throws RFIDException {
		byte[] command = new byte[data.length + 5];
		
		command[0] = (byte)0xFF;
		command[1] = (byte)0xD6;
		command[2] = (byte)0x00;
		command[3] = (byte)blockNo;
		command[4] = (byte)0x10;
		
		System.arraycopy(data, 0, command, 5, data.length);
		
		byte[] response = send(command);		
		return isValidResponse(response);
	}

	/**
	 * Get the total number of blocks on the RFID card.
	 * 
	 * @return The total number of blocks.
	 * @throws RFIDException - if an error occurred determining the total blocks.
	 */
	@Override
	public int getTotalBlocks() throws RFIDException {		
		byte[] atr = card.getATR().getBytes();
		
		if (atr.length != 20 && atr[13] != 0) {
			return BLOCKS_UNSUPPORTED;
		}
		
		switch (atr[14]) {
			case 1: return BLOCKS_MIFARE_1K;
			case 2: return BLOCKS_MIFARE_4K;
			default: return BLOCKS_UNSUPPORTED;
		}
	}
	
	/**
	 * Send a command to the RFID reader.
	 * 
	 * @param command The command to send to the reader.
	 * @return The response received from the reader.
	 * @throws RFIDException - if an error occurred issuing the command.
	 */
	protected byte[] send(byte[] command) throws RFIDException {
		byte[] response = new byte[RESPONSE_SIZE];
		ByteBuffer cmd = ByteBuffer.wrap(command);
		ByteBuffer res = ByteBuffer.wrap(response);
		
		try {
			int len = cardChannel.transmit(cmd, res);
			byte[] buf = new byte[len];
			System.arraycopy(response, 0, buf, 0, len);
			
			return buf;
		} catch (CardException e) {
			throw new RFIDException(e.getMessage());
		}
	}

	/**
	 * Determine if the response from the reader is valid.
	 * 
	 * @param response The response from the reader.
	 * @return Whether of not the response was valid.
	 */
	protected boolean isValidResponse(byte[] response) {
		if (response == null || response.length < 2) {
			return false;
		}
		
		int len = response.length;
		return response[len - 2] == -112 && response[len - 1] == 0;
	}
	
	/**
	 * Remove any unnecessary bytes from the response.
	 * 
	 * @param response The response from the reader.
	 * @return The new stripped response.
	 */
	protected byte[] stripResponse(byte[] response) {
		byte[] buf = new byte[response.length - 2];
		System.arraycopy(response, 0, buf, 0, buf.length);
		
		return buf;
	}
}
