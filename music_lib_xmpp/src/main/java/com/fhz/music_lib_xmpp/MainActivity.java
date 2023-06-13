package com.fhz.music_lib_xmpp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button mLogin;
    private Button mJoinRoom;
    private Button mSendMessage;
    private Button refreshRoomList;
    private Button reg;

    private ExecutorService executorService;
    private XmppManager mXmppManager;

    private MultiUserChat mUserChat;
    private Button mRefreshRoomList;
    private EditText msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mXmppManager = XmppManager.getInstance();
            }
        });

        mLogin.setOnClickListener(v -> {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (!XmppManager.getInstance().getConnection().isAuthenticated()) { // 判断是否登录
                        try {
                            XmppManager.getInstance().getConnection().login("feng", "123456");
                            Log.d("feng", "login: -----------");
                        } catch (XMPPException e) {
                            throw new RuntimeException(e);
                        } catch (SmackException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d(TAG, "run: 已经登录了.");
                    }
                }
            });
        });

        reg.setOnClickListener( v -> {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("username", "feng"); // 设置用户名
            attributes.put("password", "123456"); // 设置密码
            Registration reg = new Registration(attributes);
            reg.setType(IQ.Type.set); // 设置类型
            reg.setTo(XmppManager.getInstance().getConnection().getXMPPServiceDomain());// 设置发送地址
            try {
                createStanzaCollectorAndSend(reg).nextResultOrThrow();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        });

        refreshRoomList.setOnClickListener(v -> {
            List<HostedRoom> hostRooms = getHostRooms();
//            List<RosterGroup> groups = getGroups();
        });

        mJoinRoom.setOnClickListener(v -> {
            mUserChat = joinMultiUserChat("feng", "2009a");

            registerMsgListener(mUserChat, new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    System.out.println(message.getBody());
                }
            });
        });

        mSendMessage.setOnClickListener(v -> {
            String s = msg.getText().toString();
            sendGroupMessage(mUserChat, s);
        });

        //刷新消息

    }


    public void registerMsgListener(MultiUserChat muc, MessageListener messageListener) {
        muc.addMessageListener(messageListener);
    }

    public void registerStateListener(MultiUserChat muc, final StateListener stateListener) {
        muc.addParticipantStatusListener(new ParticipantStatusListener() {
            @Override
            public void joined(EntityFullJid participant) {
                stateListener.joined(participant);
            }

            @Override
            public void left(EntityFullJid participant) {
                stateListener.left(participant);
            }

            @Override
            public void kicked(EntityFullJid participant, Jid actor, String reason) {

            }

            @Override
            public void voiceGranted(EntityFullJid participant) {

            }

            @Override
            public void voiceRevoked(EntityFullJid participant) {

            }

            @Override
            public void banned(EntityFullJid participant, Jid actor, String reason) {

            }

            @Override
            public void membershipGranted(EntityFullJid participant) {

            }

            @Override
            public void membershipRevoked(EntityFullJid participant) {

            }

            @Override
            public void moderatorGranted(EntityFullJid participant) {

            }

            @Override
            public void moderatorRevoked(EntityFullJid participant) {

            }

            @Override
            public void ownershipGranted(EntityFullJid participant) {

            }

            @Override
            public void ownershipRevoked(EntityFullJid participant) {

            }

            @Override
            public void adminGranted(EntityFullJid participant) {

            }

            @Override
            public void adminRevoked(EntityFullJid participant) {

            }

            @Override
            public void nicknameChanged(EntityFullJid participant, Resourcepart newNickname) {

            }
        });
    }

    public void sendGroupMessage(MultiUserChat muc, String message) {
        try {
            Message msg = new Message();
            msg.setBody(message);

            msg.setTo(muc.getRoom());
            msg.setType(Message.Type.groupchat);
//            msg.setSubject();
            muc.sendMessage(message);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MultiUserChat joinMultiUserChat(String user, String roomsName) {
        if (XmppManager.getInstance().getConnection() == null)
            return null;
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(XmppManager.getInstance().getConnection()).getMultiUserChat(
                    JidCreate.entityBareFrom(roomsName + "@conference." + XmppManager.getInstance().getConnection().getServiceName()));

            Log.i("MultiUserChat", "joinMultiUserChat: " + user);
            // 用户加入聊天室
            muc.join(Resourcepart.from(user));

            Log.i("MultiUserChat", "会议室【" + roomsName + "】加入成功........");
            return muc;
        } catch (XMPPException | XmppStringprepException | InterruptedException |
                 SmackException e) {
            e.printStackTrace();
            Log.i("MultiUserChat", "会议室【" + roomsName + "】加入失败........");
            return null;
        }
    }


    private StanzaCollector createStanzaCollectorAndSend(IQ req) throws
            SmackException.NotConnectedException, InterruptedException {
        StanzaCollector collector = XmppManager.getInstance().getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(req.getStanzaId()), req);
        return collector;
    }

    /**
     * 获取所有组
     *
     * @return 所有组集合
     */
    public List<RosterGroup> getGroups() {
        if (XmppManager.getInstance().getConnection() == null)
            return null;
        List<RosterGroup> groupList = new ArrayList<>();
        Collection<RosterGroup> rosterGroup = Roster.getInstanceFor(XmppManager.getInstance().getConnection()).getGroups();
        for (RosterGroup aRosterGroup : rosterGroup) {
            groupList.add(aRosterGroup);
            Log.i(TAG, "getGroups: "+aRosterGroup.getName());
        }
        return groupList;
    }

    public List<HostedRoom> getHostRooms() {
        if (XmppManager.getInstance().getConnection() == null)
            return null;
        Collection<HostedRoom> hostrooms;
        List<HostedRoom> roominfos = new ArrayList<>();

        try {
            hostrooms = MultiUserChatManager.getInstanceFor(XmppManager.getInstance().getConnection()).getHostedRooms(
                    JidCreate.domainBareFrom( "@conference." +XmppManager.getInstance().getConnection().getXMPPServiceDomain()));
            for (HostedRoom entry : hostrooms) {
                roominfos.add(entry);
                Log.i("room", "名字：" + entry.getName() + " - ID:" + entry.getJid());
            }
            Log.i("room", "服务会议数量:" + roominfos.size());
        } catch (XMPPException | XmppStringprepException | InterruptedException |
                 SmackException e) {
            e.printStackTrace();
            return null;
        }
        return roominfos;
    }

    private void initView() {
        mLogin = (Button) findViewById(R.id.login);
        mJoinRoom = (Button) findViewById(R.id.joinRoom);
        mSendMessage = (Button) findViewById(R.id.sendMessage);
        refreshRoomList = (Button) findViewById(R.id.refreshRoomList);
        reg = (Button) findViewById(R.id.reg);
        mRefreshRoomList = (Button) findViewById(R.id.refreshRoomList);
        msg = (EditText) findViewById(R.id.editTextTextPersonName);
    }
}