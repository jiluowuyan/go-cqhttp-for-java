package com.zhuangxv.bot.api.support;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhuangxv.bot.api.BaseApi;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiluo
 */
public class GetMsg extends BaseApi {

    private final GetMsg.Param param;

    public GetMsg(Integer messageId) {
        this.param = new GetMsg.Param();
        this.param.setMessageId(messageId);
    }

    @Override
    public String getAction() {
        return "get_msg";
    }

    @Override
    public Object getParams() {
        return param;
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Param {
        @JSONField(name = "message_id")
        private Integer messageId;
    }
}
