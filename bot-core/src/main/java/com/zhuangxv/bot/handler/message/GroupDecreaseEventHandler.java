package com.zhuangxv.bot.handler.message;

import com.alibaba.fastjson.JSONObject;
import com.zhuangxv.bot.annotation.GroupDecreaseHandler;
import com.zhuangxv.bot.core.Bot;
import com.zhuangxv.bot.core.component.BotFactory;
import com.zhuangxv.bot.event.message.GroupDecreaseEvent;
import com.zhuangxv.bot.handler.EventHandler;
import com.zhuangxv.bot.message.Message;
import com.zhuangxv.bot.message.MessageChain;
import com.zhuangxv.bot.util.ArrayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author jiluo
 * @since 2022-05-24 10:19
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GroupDecreaseEventHandler implements EventHandler {

    @Override
    public void handle(JSONObject jsonObject, Bot bot) {
        if (!GroupDecreaseEvent.isSupport(jsonObject)) {
            return;
        }
        GroupDecreaseEvent groupDecreaseEvent = jsonObject.toJavaObject(GroupDecreaseEvent.class);
        log.debug(groupDecreaseEvent.toString());
        List<Object> resultList = BotFactory.handleMethod(bot, groupDecreaseEvent, handlerMethod -> {
            if (!handlerMethod.getMethod().isAnnotationPresent(GroupDecreaseHandler.class)) {
                return false;
            }
            GroupDecreaseHandler groupDecreaseHandler = handlerMethod.getMethod().getAnnotation(GroupDecreaseHandler.class);
            if (groupDecreaseHandler.bot() != 0 && groupDecreaseHandler.bot() != groupDecreaseEvent.getSelfId()) {
                return false;
            }
            if (groupDecreaseHandler.groupIds().length > 0 && !ArrayUtils.contain(groupDecreaseHandler.groupIds(), groupDecreaseEvent.getGroupId())) {
                return false;
            }
            if (ArrayUtils.contain(groupDecreaseHandler.excludeGroupIds(), groupDecreaseEvent.getGroupId())) {
                return false;
            }
            return true;
        }, "groupDecreaseMessage");
        for (Object result : resultList) {
            try {
                if (result instanceof Message) {
                    bot.getGroup(groupDecreaseEvent.getGroupId()).sendMessage((Message) result);
                }
                if (result instanceof MessageChain) {
                    bot.getGroup(groupDecreaseEvent.getGroupId()).sendMessage((MessageChain) result);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
