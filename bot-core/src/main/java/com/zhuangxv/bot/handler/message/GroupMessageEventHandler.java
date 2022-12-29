package com.zhuangxv.bot.handler.message;

import com.alibaba.fastjson.JSONObject;
import com.zhuangxv.bot.annotation.GroupMessageHandler;
import com.zhuangxv.bot.core.Bot;
import com.zhuangxv.bot.core.component.BotFactory;
import com.zhuangxv.bot.event.message.GroupMessageEvent;
import com.zhuangxv.bot.handler.EventHandler;
import com.zhuangxv.bot.message.CacheMessage;
import com.zhuangxv.bot.message.Message;
import com.zhuangxv.bot.message.MessageChain;
import com.zhuangxv.bot.message.MessageTypeHandle;
import com.zhuangxv.bot.message.support.AtMessage;
import com.zhuangxv.bot.util.ArrayUtils;
import com.zhuangxv.bot.utilEnum.IgnoreItselfEnum;
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
public class GroupMessageEventHandler implements EventHandler {

    @Override
    public void handle(JSONObject jsonObject, Bot bot) {
        if (!GroupMessageEvent.isSupport(jsonObject)) {
            return;
        }
        GroupMessageEvent groupMessageEvent = jsonObject.toJavaObject(GroupMessageEvent.class);
        MessageChain messageChain = new MessageChain();
        for (int i = 0; i < groupMessageEvent.getMessage().size(); i++) {
            messageChain.add(MessageTypeHandle.getMessage(groupMessageEvent.getMessage().getJSONObject(i)));
        }
        log.debug(messageChain.toMessageString());
        CacheMessage cacheMessage = new CacheMessage();
        cacheMessage.setSenderId(groupMessageEvent.getUserId());
        cacheMessage.setMessageChain(messageChain);
        bot.pushGroupCacheMessageChain(groupMessageEvent.getGroupId(), groupMessageEvent.getMessageId(), cacheMessage);
        List<Object> resultList = BotFactory.handleMethod(bot, groupMessageEvent, handlerMethod -> {
            if (!handlerMethod.getMethod().isAnnotationPresent(GroupMessageHandler.class)) {
                return false;
            }
            GroupMessageHandler groupMessageHandler = handlerMethod.getMethod().getAnnotation(GroupMessageHandler.class);
            if (groupMessageHandler.bot() != 0 && groupMessageHandler.bot() != groupMessageEvent.getSelfId()) {
                return false;
            }
            if (groupMessageHandler.ignoreItself().equals(IgnoreItselfEnum.IGNORE_ITSELF) && "message_sent".equals(groupMessageEvent.getPostType())) {
                return false;
            } else if (groupMessageHandler.ignoreItself().equals(IgnoreItselfEnum.ONLY_ITSELF) && !"message_sent".equals(groupMessageEvent.getPostType())){
                return false;
            }
            if (groupMessageHandler.groupIds().length > 0 && !ArrayUtils.contain(groupMessageHandler.groupIds(), groupMessageEvent.getGroupId())) {
                return false;
            }
            if (ArrayUtils.contain(groupMessageHandler.excludeGroupIds(), groupMessageEvent.getGroupId())) {
                return false;
            }
            if (groupMessageHandler.senderIds().length > 0 && !ArrayUtils.contain(groupMessageHandler.senderIds(), groupMessageEvent.getUserId())) {
                return false;
            }
            if (ArrayUtils.contain(groupMessageHandler.excludeSenderIds(), groupMessageEvent.getUserId())) {
                return false;
            }
            if (groupMessageHandler.isAt()) {
                boolean isAt = false;
                for (Message message : messageChain) {
                    if (message instanceof AtMessage && Long.parseLong(((AtMessage) message).getQq()) == groupMessageEvent.getSelfId()) {
                        isAt = true;
                    }
                }
                if (!isAt) return false;
            }
            return "none".equals(groupMessageHandler.regex()) || messageChain.toString().matches(groupMessageHandler.regex());
        }, "message");
        for (Object result : resultList) {
            try {
                if (result instanceof Message) {
                    bot.getGroup(groupMessageEvent.getGroupId()).sendMessage((Message) result);
                }
                if (result instanceof MessageChain) {
                    bot.getGroup(groupMessageEvent.getGroupId()).sendMessage((MessageChain) result);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
