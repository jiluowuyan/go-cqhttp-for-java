package com.zhuangxv.bot.event.message;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.zhuangxv.bot.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author jiluo
 * @since 2024-02-05 17:16
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupDecreaseEvent extends BaseEvent {

    @JSONField(name = "notice_type")
    private String noticeType;

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "operator_id")
    private Long operatorId;


    public static boolean isSupport(JSONObject jsonObject) {
        return ("notice".equals(jsonObject.getString("post_type"))
                && "group_decrease".equals(jsonObject.getString("notice_type")));
    }

}
