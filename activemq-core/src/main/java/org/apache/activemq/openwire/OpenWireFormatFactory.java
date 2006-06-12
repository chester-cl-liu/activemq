/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
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
package org.apache.activemq.openwire;

import org.apache.activeio.command.WireFormat;
import org.apache.activeio.command.WireFormatFactory;
import org.apache.activemq.command.WireFormatInfo;

/**
 * @version $Revision$
 */
public class OpenWireFormatFactory implements WireFormatFactory {

	//
	// The default values here are what the wire format changes to after a default negotiation.
	//
	
    private int version=1;
    private boolean stackTraceEnabled=true;
    private boolean tcpNoDelayEnabled=true;
    private boolean cacheEnabled=true;
    private boolean tightEncodingEnabled=true;
    private boolean sizePrefixDisabled=false;
    private long maxInactivityDuration=30*1000;
    
    public WireFormat createWireFormat() {
		WireFormatInfo info = new WireFormatInfo();
		info.setVersion(version);
		
        try {
			info.setStackTraceEnabled(stackTraceEnabled);
			info.setCacheEnabled(cacheEnabled);
			info.setTcpNoDelayEnabled(tcpNoDelayEnabled);
			info.setTightEncodingEnabled(tightEncodingEnabled);
			info.setSizePrefixDisabled(sizePrefixDisabled);
            info.seMaxInactivityDuration(maxInactivityDuration);
		} catch (Exception e) {
			IllegalStateException ise = new IllegalStateException("Could not configure WireFormatInfo");
            ise.initCause(e);
            throw ise;
		}
		
        OpenWireFormat f = new OpenWireFormat();
        f.setPreferedWireFormatInfo(info);
        return f;
    }

    public boolean isStackTraceEnabled() {
        return stackTraceEnabled;
    }

    public void setStackTraceEnabled(boolean stackTraceEnabled) {
        this.stackTraceEnabled = stackTraceEnabled;
    }

    public boolean isTcpNoDelayEnabled() {
        return tcpNoDelayEnabled;
    }

    public void setTcpNoDelayEnabled(boolean tcpNoDelayEnabled) {
        this.tcpNoDelayEnabled = tcpNoDelayEnabled;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public boolean isTightEncodingEnabled() {
        return tightEncodingEnabled;
    }

    public void setTightEncodingEnabled(boolean tightEncodingEnabled) {
        this.tightEncodingEnabled = tightEncodingEnabled;
    }

	public boolean isSizePrefixDisabled() {
		return sizePrefixDisabled;
	}

	public void setSizePrefixDisabled(boolean sizePrefixDisabled) {
		this.sizePrefixDisabled = sizePrefixDisabled;
	}

    public long getMaxInactivityDuration() {
        return maxInactivityDuration;
    }

    public void setMaxInactivityDuration(long maxInactivityDuration) {
        this.maxInactivityDuration = maxInactivityDuration;
    }
}
