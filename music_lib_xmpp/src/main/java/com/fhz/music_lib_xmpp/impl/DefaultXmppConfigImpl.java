package com.fhz.music_lib_xmpp.impl;


import com.fhz.music_lib_xmpp.BuildConfig;
import com.fhz.music_lib_xmpp.contract.IXmppConfig;

/**
 * 默认XmppConfig实现类
 */
public class DefaultXmppConfigImpl implements IXmppConfig {
    @Override
    public String getDomainName() {
        return BuildConfig.xmppDoMain;

    }

    @Override
    public String getHostAddress() {
        return BuildConfig.xmppHostAddress;
    }

    @Override
    public int getPort() {
        return BuildConfig.xmppPort;
    }
}
