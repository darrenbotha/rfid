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

import java.util.List;

import javax.smartcardio.CardTerminal;

/**
 * A general interface that encapsulates the functions that the
 * RFID reader implementations should implement at a minimum.
 * 
 * @author Darren Botha
 */
public interface RFIDReader {
	/**
	 * Connect to the RFID reader.
	 * 
	 * @return Whether or not the connection was successful.
	 * @throws RFIDException - if there is no reader present.
	 */
	boolean connect() throws RFIDException;
	
	/**
	 * Disconnect from an active connected RFID reader.
	 * 
	 * @throws RFIDException - if the disconnection failed.
	 */
	void disconnect() throws RFIDException;
	
	/**
	 * List all the available RFID readers on the system.
	 * 
	 * @return List containing all the RFID readers.
	 * @throws RFIDException - if an error occurred finding the RFID readers.
	 */
	List<CardTerminal> listAvailableReaders() throws RFIDException;
	
	/**
	 * Authenticate to a block on the RFID card on the reader.
	 * 
	 * @param blockNo The block number to authenticate to.
	 * @return Whether or not the authentication was successful.
	 * @throws RFIDException - if an error occurred authenticating.
	 */
	boolean authenticate(final int blockNo) throws RFIDException;
	
	/**
	 * Get the unique identifier of the RFID card on the reader.
	 * 
	 * @return The 4 byte array of the unique identifier.
	 * @throws RFIDException - if an error occurred reading the card identifier.
	 */
	byte[] getCardIdentifier() throws RFIDException;
	
	/**
	 * Read the all the data on a single block on the RFID card.
	 * 
	 * @param blockNo The block number to read.
	 * @return The byte array containing the data.
	 * @throws RFIDException - if an error occurred reading the block.
	 */
	byte[] read(final int blockNo) throws RFIDException;
	
	/**
	 * Write data to a single block on the RFID card.
	 * 
	 * @param blockNo The block number to write.
	 * @param data The data to write to the block.
	 * @return Whether or not the write was successful.
	 * @throws RFIDException - if an error occurred writing to the block.
	 */
	boolean write(final int blockNo, final byte[] data) throws RFIDException;
	
	/**
	 * Get the total number of blocks on the RFID card.
	 * 
	 * @return The total number of blocks.
	 * @throws RFIDException - if an error occurred determining the total blocks.
	 */
	int getTotalBlocks() throws RFIDException;
}
