package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast{
    private String senderName; //Which service sent the message
    private String error; //What error occured
    private String faultySensor; //Which sensor caused the error

    public CrashedBroadcast(String senderName, String error, String faultySensor){
        this.senderName = senderName;
        this.error = error;
        this.faultySensor = faultySensor;
    }

    public String getSenderName(){
        return senderName;
    }

    public String getError(){
        return error;
    }

    public String getFaultySensor(){
        return faultySensor;
    }
}
