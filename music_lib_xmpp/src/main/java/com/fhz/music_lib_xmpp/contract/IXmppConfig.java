package com.fhz.music_lib_xmpp.contract;

/**
 * Xmpp 配置接口
 */
public interface IXmppConfig {
    /**
     * 获取域名
     * @return
     */
    String getDomainName();

    /**
     * 获取服务器地址
     * @return
     */
    String getHostAddress();

    /**
     * 获取连接端口
     * @return
     */
    int getPort();
}
