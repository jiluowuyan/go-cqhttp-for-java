package com.zhuangxv.bot.handler.message;

import com.alibaba.fastjson.JSONObject;
import com.zhuangxv.bot.annotation.GroupIncreaseHandler;
import com.zhuangxv.bot.core.Bot;
import com.zhuangxv.bot.core.component.BotFactory;
import com.zhuangxv.bot.event.message.GroupIncreaseEvent;
import com.zhuangxv.bot.handler.EventHandler;
import com.zhuangxv.bot.message.Message;
import com.zhuangxv.bot.message.MessageChain;
import com.zhuangxv.bot.util.ArrayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GroupIncreaseEventHandler implements EventHandler {

    @Override
    public void handle(JSONObject jsonObject, Bot bot) {
        if (!GroupIncreaseEvent.isSupport(jsonObject)) {
            return;
        }
        GroupIncreaseEvent groupIncreaseEvent = jsonObject.toJavaObject(GroupIncreaseEvent.class);
        log.debug(groupIncreaseEvent.toString());
        List<Object> resultList = BotFactory.handleMethod(bot, groupIncreaseEvent, handlerMethod -> {
            if (!handlerMethod.getMethod().isAnnotationPresent(GroupIncreaseHandler.class)) {
                return false;
            }
            GroupIncreaseHandler groupIncreaseHandler = handlerMethod.getMethod().getAnnotation(GroupIncreaseHandler.class);
            if (groupIncreaseHandler.bot() != 0 && groupIncreaseHandler.bot() != groupIncreaseEvent.getSelfId()) {
                return false;
            }
            if (groupIncreaseHandler.groupIds().length > 0 && !ArrayUtils.contain(groupIncreaseHandler.groupIds(), groupIncreaseEvent.getGroupId())) {
                return false;
            }
            if (ArrayUtils.contain(groupIncreaseHandler.excludeGroupIds(), groupIncreaseEvent.getGroupId())) {
                return false;
            }
            return true;
        }, "groupIncreaseMessage");
        for (Object result : resultList) {
            try {
                if (result instanceof Message) {
                    bot.getGroup(groupIncreaseEvent.getGroupId()).sendMessage((Message) result);
                }
                if (result instanceof MessageChain) {
                    bot.getGroup(groupIncreaseEvent.getGroupId()).sendMessage((MessageChain) result);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
