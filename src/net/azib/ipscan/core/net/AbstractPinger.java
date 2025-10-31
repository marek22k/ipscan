/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.Plugin;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;

import java.io.IOException;

/**
 * Convenience base class for built-in fetchers
 *
 * @author Anton Keks
 */
public abstract class AbstractPinger implements Plugin {
	public String getName() {
		return Labels.getLabel(getId());
	}
	
    abstract public PingResult ping(ScanningSubject subject, int count) throws IOException;
}
