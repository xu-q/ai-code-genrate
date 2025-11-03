package com.aicodegenerate.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

public class CacheKeyUtils {

    public static String generateKey(Object object) {
        if (object == null) {
            return DigestUtil.md5Hex("null");
        }
        String jsonStr = JSONUtil.toJsonStr(object);
        return DigestUtil.md5Hex(jsonStr);
    }
}
