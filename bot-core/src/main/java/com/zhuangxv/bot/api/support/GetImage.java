package com.zhuangxv.bot.api.support;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhuangxv.bot.api.BaseApi;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiluo
 */
public class GetImage extends BaseApi {

    private final GetImage.Param param;

    public GetImage(String imageId) {
        this.param = new GetImage.Param();
        this.param.setFile(imageId);
    }

    @Override
    public String getAction() {
        return "get_image";
    }

    @Override
    public Object getParams() {
        return param;
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Param {
        @JSONField(name = "file")
        private String file;
    }
}
