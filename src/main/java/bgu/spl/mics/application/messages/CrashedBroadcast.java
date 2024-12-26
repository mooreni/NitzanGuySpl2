package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast{
    private String senderName; //Which object crashed

    public CrashedBroadcast(String senderName){
        this.senderName = senderName;
    }

    public String getSenderName(){
        return senderName;
    }
}
