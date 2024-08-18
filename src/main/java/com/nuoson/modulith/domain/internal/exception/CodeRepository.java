package com.nuoson.modulith.domain.internal.exception;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 错误码查询类
 **/
public class CodeRepository {

    private static final Map<Object, Code> CODE_MAP = new ConcurrentHashMap<>();

    public static Code get(Object object) {
        if (CODE_MAP.containsKey(object)) {
            return CODE_MAP.get(object);
        }
        try {
            Enum<?> codeEnum = (Enum<?>) object;
            CodePrefix codePrefix = object.getClass().getAnnotation(CodePrefix.class);
            C c = object.getClass().getField(codeEnum.name()).getAnnotation(C.class);
            Code code = new Code();
            String cod = c != null ? c.value() : String.valueOf(codeEnum.ordinal());
            code.setCode(codePrefix != null ? String.format("%s" + "%s", codePrefix.value(), cod) : cod);
            code.setMsg(c != null && c.msg().length() > 0 ? c.msg() : codeEnum.name());
            CODE_MAP.put(object, code);
            return code;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
