package com.neo.domain.weixin.model.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("xml")
public class MessageTextEntity {

    @JacksonXmlCData
    @JsonProperty("MsgId")
    private String msgId;
    @JacksonXmlCData
    @JsonProperty("ToUserName")
    private String toUserName;
    @JacksonXmlCData
    @JsonProperty("FromUserName")
    private String fromUserName;
    @JacksonXmlCData
    @JsonProperty("CreateTime")
    private String createTime;
    @JacksonXmlCData
    @JsonProperty("MsgType")
    private String msgType;
    @JacksonXmlCData
    @JsonProperty("Content")
    private String content;
    @JacksonXmlCData
    @JsonProperty("Event")
    private String event;
    @JacksonXmlCData
    @JsonProperty("EventKey")
    private String eventKey;

    public MessageTextEntity() {
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }
}