package com.fhz.music_lib_xmpp;

import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

/**
 * @author hahajing 企鹅：444511958
 * @version 1.0.0
 * @createDate 2022/8/30 9:45
 * @description
 * @updateUser hahajing
 * @updateDate 2022/8/30 9:45
 * @updateRemark
 */
public class StateListener implements ParticipantStatusListener {

    @Override
    public void joined(EntityFullJid participant) {

    }

    @Override
    public void left(EntityFullJid participant) {

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
}
