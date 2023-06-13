package com.fhz.music_lib_xmpp;

import android.util.Log;

import com.fhz.music_lib_xmpp.contract.IXmppConfig;
import com.fhz.music_lib_xmpp.impl.DefaultXmppConfigImpl;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 时间:2023/5/29
 *
 * @author Mr.Feng
 * 简述: xmpp的管理类 是个代理.
 */
public class XmppManager {
    private static final String TAG = "XmppManager";
    private AbstractXMPPConnection mConnection;
    private volatile static XmppManager instance=null;
    private XmppManager(){
        //连接IM服务器
        connect();

    }

    private void connect() {
        try {
            XMPPTCPConnectionConfiguration configuration=XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain("desktop-iugoaaq.mshome.net")
                    .setHostAddress(InetAddress.getByName("10.161.1.94"))
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)//不用ssl
                    .setCompressionEnabled(false)
                    .setDebuggerEnabled(true)
                    //.setSendPresence(false)//设置离线状态获取离线消息
                    .build();

            //设置需要经过同意才可以加为好友
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
            mConnection = new XMPPTCPConnection(configuration);

            mConnection.connect();// 连接, 可设置监听
            mConnection.addConnectionListener(new XMPPConnectionListener());


        } catch (XmppStringprepException e) {
            Log.e(TAG, e.getMessage(),e);
        } catch (UnknownHostException e) {
            Log.e(TAG, "connect: ", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "connect: ", e);
        } catch (IOException e) {
            Log.e(TAG, "connect: ", e);
        } catch (SmackException e) {
            Log.e(TAG, "connect: ", e);
        } catch (XMPPException e) {
            Log.e(TAG, "connect: ", e);
        }
    }

    public AbstractXMPPConnection getConnection(){
        return this.mConnection;
    }

    private class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(XMPPConnection connection) {
            Log.i(TAG, "connected: ");
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Log.i(TAG, "authenticated: ");
//            addRosterListener();
            //开启心跳处理
            //startHeartDump();
//            LogUtils.i("start heartdump...");
        }

        @Override
        public void connectionClosed() {
            Log.d(TAG, "connectionClosed: ");
            //开启心跳处理
            //startHeartDump();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.i(TAG, "connectionClosedOnError: ");
            //开启心跳处理
            //startHeartDump();
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i(TAG, "reconnectionSuccessful: ");
        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.i(TAG, "reconnectingIn: ");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.i(TAG, "reconnectionFailed: ");
        }
    }



    public static XmppManager getInstance(){
        if (instance==null){
            synchronized (XmppManager.class){
                if (instance==null){
                    instance=new XmppManager();
                }
            }
        }
        return instance;
    }

    public IXmppConfig getXmppConfig(){
        if (null==xmppConfig){
            xmppConfig=new DefaultXmppConfigImpl();
        }
        return xmppConfig;
    }

    /**
     * Xmpp 配置信息
     */
    private IXmppConfig xmppConfig;
}   
