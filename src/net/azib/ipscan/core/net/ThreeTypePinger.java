// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package net.azib.ipscan.core.net;

import java.io.IOException;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.Plugin;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.JavaPinger;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.TCPPinger;
import net.azib.ipscan.core.net.UDPPinger;

/**
 * Pinger, which combines Java build-in pinger, UDP Pinger and TCP Pinger
 */
public class ThreeTypePinger implements Pinger, Plugin {
    private final JavaPinger javaPinger;
    private final UDPPinger udpPinger;
    private final TCPPinger tcpPinger;

    public ThreeTypePinger(
            final JavaPinger javaPinger, final TCPPinger tcpPinger, final UDPPinger udpPinger
    ) {
        super();
        this.javaPinger = javaPinger;
        this.udpPinger = udpPinger;
        this.tcpPinger = tcpPinger;
    }

    @Override
    public String getId() {
        return "pinger.threeTypePinger";
    }

    @Override
    public String getName() {
        return Labels.getLabel("pinger.threeTypePinger");
    }

    @Override
    public PingResult ping(final ScanningSubject subject, final int count) throws IOException {
        // try Java Build-in first - as it could use ICMP
        // minimum three tries to prevent packet loss in unreliable networks
        final int javaBuiltinInitialCount = Math.max(1, count / 3);
        final PingResult javaBuiltinResult = this.javaPinger.ping(subject, javaBuiltinInitialCount);
        if (javaBuiltinResult.isAlive()) {
            return javaBuiltinResult.merge(javaPinger.ping(subject, count - javaBuiltinInitialCount));
        }

        // try UDP second - it should be more reliable than TCP, but less than ICMP
        // minimum three tries to prevent packet loss in unreliable networks
        final int udpCountInitialCount = Math.max(1, count / 3);
        final PingResult udpResult = udpPinger.ping(subject, udpCountInitialCount);
        if (udpResult.isAlive()) {
            return udpResult.merge(udpPinger.ping(subject, count - udpCountInitialCount));
        }

        // fallback to TCP - it may detect some hosts Java Built-in, UDP cannot
        return tcpPinger.ping(subject, count);
    }
}
