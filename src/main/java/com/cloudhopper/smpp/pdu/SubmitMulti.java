/**
 * 
 */
package com.cloudhopper.smpp.pdu;

/*
 * #%L
 * ch-smpp
 * %%
 * Copyright (C) 2009 - 2013 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.cloudhopper.smpp.util.ChannelBufferUtil;
import com.cloudhopper.smpp.util.PduUtil;

/**
 * @author Amit Bhayani
 * 
 */
public class SubmitMulti extends BaseSm<SubmitMultiResp> {

	private int numberOfDest;

	private List<Address> destAddresses = new ArrayList<Address>();
	private List<String> destDistributionList = new ArrayList<String>();

	/**
	 * @param commandId
	 * @param name
	 */
	public SubmitMulti() {
		super(SmppConstants.CMD_ID_SUBMIT_MULTI, "submit_multi");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cloudhopper.smpp.pdu.PduRequest#createResponse()
	 */
	@Override
	public SubmitMultiResp createResponse() {
		SubmitMultiResp resp = new SubmitMultiResp();
		resp.setSequenceNumber(this.getSequenceNumber());
		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cloudhopper.smpp.pdu.PduRequest#getResponseClass()
	 */
	@Override
	public Class<SubmitMultiResp> getResponseClass() {
		return SubmitMultiResp.class;
	}

	@Override
	public Address getDestAddress() {
		return null;
	}

	@Override
	public void setDestAddress(Address value) {

	}

	public void addDestAddresses(Address address)
			throws SmppInvalidArgumentException {
		this.numberOfDest++;
		this.destAddresses.add(address);
	}

	public void addDestDestributionListName(String name) {
		this.numberOfDest++;
		this.destDistributionList.add(name);
	}

	public List<Address> getDestAddresses() {
		return this.destAddresses;
	}

	public List<String> getDestDestributionListName() {
		return this.destDistributionList;
	}
	
	public int getNumberOfDest(){
		return this.numberOfDest;
	}
	
    @Override
    public void readBody(ChannelBuffer buffer) throws UnrecoverablePduException, RecoverablePduException {
        this.serviceType = ChannelBufferUtil.readNullTerminatedString(buffer);
        this.sourceAddress = ChannelBufferUtil.readAddress(buffer);

        this.numberOfDest = buffer.readByte() & 0xFF;

        for(int count=0;count<this.numberOfDest; count++){
        	byte flag = buffer.readByte();
        	if(flag==SmppConstants.SME_ADDRESS){
        		this.destAddresses.add(ChannelBufferUtil.readAddress(buffer));
        	} else if(flag==SmppConstants.DISTRIBUTION_LIST_NAME){
        		this.destDistributionList.add(ChannelBufferUtil.readNullTerminatedString(buffer));
        	}
        }
        
        this.esmClass = buffer.readByte();
        this.protocolId = buffer.readByte();
        this.priority = buffer.readByte();
        this.scheduleDeliveryTime = ChannelBufferUtil.readNullTerminatedString(buffer);
        this.validityPeriod = ChannelBufferUtil.readNullTerminatedString(buffer);
        this.registeredDelivery = buffer.readByte();
        this.replaceIfPresent = buffer.readByte();
        this.dataCoding = buffer.readByte();
        this.defaultMsgId = buffer.readByte();
        // this is always an unsigned version of the short message length
        short shortMessageLength = buffer.readUnsignedByte();
        this.shortMessage = new byte[shortMessageLength];
        buffer.readBytes(this.shortMessage);
    }

	@Override
	public void writeBody(ChannelBuffer buffer)
			throws UnrecoverablePduException, RecoverablePduException {
		ChannelBufferUtil.writeNullTerminatedString(buffer, this.serviceType);
		ChannelBufferUtil.writeAddress(buffer, this.sourceAddress);

		buffer.writeByte(this.numberOfDest);
		
		for(Address adress : this.destAddresses){
			buffer.writeByte(SmppConstants.SME_ADDRESS);
			ChannelBufferUtil.writeAddress(buffer, adress);
		}

		for(String s : this.destDistributionList){
			buffer.writeByte(SmppConstants.DISTRIBUTION_LIST_NAME);
			ChannelBufferUtil.writeNullTerminatedString(buffer, s);
			
		}

		buffer.writeByte(this.esmClass);
		buffer.writeByte(this.protocolId);
		buffer.writeByte(this.priority);
		ChannelBufferUtil.writeNullTerminatedString(buffer,
				this.scheduleDeliveryTime);
		ChannelBufferUtil
				.writeNullTerminatedString(buffer, this.validityPeriod);
		buffer.writeByte(this.registeredDelivery);
		buffer.writeByte(this.replaceIfPresent);
		buffer.writeByte(this.dataCoding);
		buffer.writeByte(this.defaultMsgId);
		buffer.writeByte((byte) getShortMessageLength());
		if (this.shortMessage != null) {
			buffer.writeBytes(this.shortMessage);
		}
	}
	
    @Override
    public int calculateByteSizeOfBody() {
        int bodyLength = 0;
        bodyLength += PduUtil.calculateByteSizeOfNullTerminatedString(this.serviceType);
        bodyLength += PduUtil.calculateByteSizeOfAddress(this.sourceAddress);
        
        bodyLength +=1; //number_of_dests
        
        for(Address adress : this.destAddresses){
        	bodyLength += 1;//Flag
        	bodyLength += PduUtil.calculateByteSizeOfAddress(adress);
        }
        
		for(String s : this.destDistributionList){
			bodyLength += 1;//Flag
			bodyLength += PduUtil.calculateByteSizeOfNullTerminatedString(s);
		}
		
        bodyLength += 3;    // esmClass, priority, protocolId
        bodyLength += PduUtil.calculateByteSizeOfNullTerminatedString(this.scheduleDeliveryTime);
        bodyLength += PduUtil.calculateByteSizeOfNullTerminatedString(this.validityPeriod);
        bodyLength += 5;    // regDelivery, replace, dataCoding, defaultMsgId, messageLength bytes
        bodyLength += getShortMessageLength();
        return bodyLength;
    }
}
