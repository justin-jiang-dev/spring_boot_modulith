package com.nuoson.modulith.infra.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class JsonUtils {
    // #region[Variables] -- ** ORDER BY NAME **

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper mapper = new JsonMapper();

    /**
     * 来自于JsonPath，用于属性值查找，属性名称替换等方法，此类为线程安全
     */
    private static ParseContext jsonParseContext = null;

    private static Set<JsonReadFeature> JSON_READ_FEATURES_ENABLED = null;

    // #endregion

    // #region[Properties] -- ** ORDER BY NAME **

    // #endregion

    // #region[Constructor]
    static {
        try {
            JSON_READ_FEATURES_ENABLED = new HashSet<>();
            // 允许在JSON中使用Java注释
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_JAVA_COMMENTS);
            // 允许 json 存在没用双引号括起来的 field
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES);
            // 允许 json 存在使用单引号括起来的 field
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_SINGLE_QUOTES);
            // 允许 json 存在没用引号括起来的 ascii 控制字符
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS);
            // 允许 json number 类型的数存在前导 0 (例: 0001)
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS);
            // 允许 json 存在 NaN, INF, -INF 作为 number 类型
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS);
            // 允许 只有Key没有Value的情况
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_MISSING_VALUES);
            // 允许数组json的结尾多逗号
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_TRAILING_COMMA);
            // 初始化
            initMapper(mapper, JSON_READ_FEATURES_ENABLED);
            // 初始化JsonPath
            initJsonPath();
        } catch (Exception e) {
            logger.error("jackson config error", e);
        }
    }

    private static ObjectMapper initMapper(ObjectMapper mapper, Set<JsonReadFeature> jsonReadFeatures) {
        if (jsonReadFeatures == null || jsonReadFeatures.isEmpty()) {
            jsonReadFeatures = JSON_READ_FEATURES_ENABLED;
        }
        if (mapper == null) {
            JsonMapper.Builder builder = JsonMapper.builder().enable(jsonReadFeatures.toArray(new JsonReadFeature[0]));
            mapper = builder.build();
        }
        for (JsonReadFeature f : jsonReadFeatures) {
            mapper.enable(f.mappedFeature());
        }
        return initMapperConfig(mapper);
    }

    public static ObjectMapper initMapperConfig(ObjectMapper objectMapper) {
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        objectMapper.setDateFormat(new SimpleDateFormat(dateTimeFormat));
        // 配置序列化级别
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 配置JSON缩进支持
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        // 允许单个数值当做数组处理
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        // 禁止重复键, 抛出异常
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        // 禁止使用int代表Enum的order()來反序列化Enum, 抛出异常
        objectMapper.enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        // 有属性不能映射的时候不报错
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 对象为空时不抛异常
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 时间格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 允许未知字段
        objectMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        // 序列化BigDecimal时之间输出原始数字还是科学计数, 默认false, 即是否以toPlainString()科学计数方式来输出
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        // 识别Java8时间
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule
                .addSerializer(LocalDateTime.class,
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .addDeserializer(LocalDateTime.class,
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    /**
     * 使用Jackson Provider初始化json path
     */
    private static void initJsonPath() {
        jsonParseContext = JsonPath.using(Configuration.builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonNodeJsonProvider()).build());
    }
    // #endregion

    // #region[Methods] -- deserialized

    /**
     * 
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    /**
     * JSON反序列化
     * 
     * @param url URL
     * @param type 类型
     * @return 反序列化后的对象
     */
    public static <V> V from(URL url, Class<V> type) {
        try {
            return mapper.readValue(url, type);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, url: %s, type: %s", url.getPath(), type.toString()), e);
        }
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(URL url, TypeReference<V> type) {
        try {
            return mapper.readValue(url, type);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, url: %s, type: %s", url.getPath(), type.toString()), e);
        }
    }

    /**
     * JSON反序列化（List）
     */
    public static <V> List<V> fromList(URL url, Class<V> type) {
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(url, collectionType);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, url: %s, type: %s", url.getPath(), type.toString()), e);
        }
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(InputStream inputStream, Class<V> type) {
        try {
            return mapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, type: %s", type.toString()), e);
        }
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(InputStream inputStream, TypeReference<V> type) {
        try {
            return mapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, type: %s", type.toString()), e);
        }
    }

    /**
     * JSON反序列化（List）
     */
    public static <V> List<V> fromList(InputStream inputStream, Class<V> type) {
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(inputStream, collectionType);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, type: %s", type.toString()), e);
        }
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(File file, Class<V> type) {
        try {
            return mapper.readValue(file, type);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, url: %s, type: %s", file.getPath(), type.toString()), e);
        }
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(File file, TypeReference<V> type) {
        try {
            return mapper.readValue(file, type);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, url: %s, type: %s", file.getPath(), type.toString()), e);
        }
    }

    /**
     * JSON反序列化（List）
     */
    public static <V> List<V> fromList(File file, Class<V> type) {
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(file, collectionType);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, url: %s, type: %s", file.getPath(), type.toString()), e);
        }
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(String json, Class<V> type) {
        return from(json, (Type) type);
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(String json, TypeReference<V> type) {
        return from(json, type.getType());
    }

    /**
     * JSON反序列化
     */
    public static <V> V from(String json, Type type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson from error, json: %s, type: %s", json, type.toString()), e);
        }
    }

    /**
     * JSON反序列化（List）
     */
    public static <V> List<V> fromList(String json, Class<V> type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(json, collectionType);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson fromList error, json: %s, type: %s", json, type.toString()), e);
        }
    }

    /**
     * JSON反序列化（Map）
     */
    public static Map<String, Object> fromMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw new RuntimeException(String.format("jackson fromMap error, json: %s", json), e);
        }
    }
    // #endregion

    // #region[Methods] -- serialized
    /**
     * 序列化为JSON
     */
    public static <V> String to(List<V> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson to error with list param", e);
        }
    }

    /**
     * 序列化为JSON
     */
    public static <V> String to(V v) {
        try {
            return mapper.writeValueAsString(v);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("jackson to error, param: %s", v.toString()), e);
        }
    }

    /**
     * 序列化为JSON
     */
    public static <V> void toFile(String path, List<V> list) {
        try (Writer writer = new FileWriter(new File(path), true)) {
            mapper.writer().writeValues(writer).writeAll(list);
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson toFile error with list param, path: %s", path), e);
        }
    }

    /**
     * 序列化为JSON
     */
    public static <V> void toFile(String path, V v) {
        try (Writer writer = new FileWriter(new File(path), true)) {
            mapper.writer().writeValues(writer).write(v);
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson toFile error, path: %s, v: %s", path, v.toString()), e);
        }
    }
    // #endregion

    // #region[Methods] -- Read/Update/Remove Property from Json by Key
    /**
     * 从json串中获取某个字段
     * 
     * @return String，默认为 null
     */
    public static String getAsString(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return null;
            }
            return getAsString(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get string error, json: %s, key: %s", json, key), e);
        }
    }

    public static String getAsText(JsonNode jsonNode, String key) {
        if (jsonNode == null) {
            return null;
        }
        try {
            JsonNode valueNode = jsonNode.get(key);
            if (null == valueNode) {
                return null;
            }
            return valueNode.asText();
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("jackson get string error, json: %s, key: %s", jsonNode.toString(), key), e);
        }
    }

    /**
     * 根据指定属性的Json Path，返回属性的文本值（如果指定的属性为数组或对象，则返回空）
     * 
     * @param jsonNode -- json对象
     * @param jsonPath -- 属性路径，格式参见：https://github.com/json-path/JsonPath
     * @return
     */
    public static String getAsStringByJsonPath(JsonNode jsonNode, String jsonPath) {
        JsonNode targetNode = JsonUtils.jsonParseContext.parse(jsonNode).read(jsonPath);
        if (targetNode == null || targetNode.isContainerNode()) {
            return null;
        }
        return targetNode.asText();
    }

    /**
     * 根据指定属性的Json Path，返回属性的文本值（如果指定的属性为数组或对象，则返回空）
     * 
     * @param json -- json字符串
     * @param jsonPath -- 属性路径，格式参见：https://github.com/json-path/JsonPath
     * @return
     */
    public static String getAsStringByJsonPath(String json, String jsonPath) {
        return JsonUtils.getAsStringByJsonPath(JsonUtils.from(json, JsonNode.class), jsonPath);
    }

    /**
     * 判断字段是否为空
     * 
     * @param jsonNode
     * @param key
     * @return
     */
    public static Boolean isEmpty(JsonNode jsonNode, String key) {
        if (jsonNode == null) {
            return true;
        }
        try {
            JsonNode valueNode = jsonNode.get(key);
            if (null == valueNode) {
                return true;
            }
            return valueNode.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("jackson isEmpty error, json: %s, key: %s", jsonNode.toString(), key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return int，默认为 0
     */
    public static int getAsInt(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return 0;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return 0;
            }
            return jsonNode.isInt() ? jsonNode.intValue() : Integer.parseInt(getAsString(jsonNode));
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get int error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return long，默认为 0
     */
    public static long getAsLong(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return 0L;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return 0L;
            }
            return jsonNode.isLong() ? jsonNode.longValue() : Long.parseLong(getAsString(jsonNode));
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get long error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return double，默认为 0.0
     */
    public static double getAsDouble(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return 0.0;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return 0.0;
            }
            return jsonNode.isDouble() ? jsonNode.doubleValue() : Double.parseDouble(getAsString(jsonNode));
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get double error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return BigInteger，默认为 0.0
     */
    public static BigInteger getAsBigInteger(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return new BigInteger(String.valueOf(0.00));
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return new BigInteger(String.valueOf(0.00));
            }
            return jsonNode.isBigInteger() ? jsonNode.bigIntegerValue() : new BigInteger(getAsString(jsonNode));
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get big integer error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return BigDecimal，默认为 0.00
     */
    public static BigDecimal getAsBigDecimal(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return new BigDecimal("0.00");
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return new BigDecimal("0.00");
            }
            return jsonNode.isBigDecimal() ? jsonNode.decimalValue() : new BigDecimal(getAsString(jsonNode));
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get big decimal error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return boolean, 默认为false
     */
    public static boolean getAsBoolean(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        try {
            String numberOne = "1";
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return false;
            }
            if (jsonNode.isBoolean()) {
                return jsonNode.booleanValue();
            } else {
                if (jsonNode.isTextual()) {
                    String textValue = jsonNode.textValue();
                    if (numberOne.equals(textValue)) {
                        return true;
                    } else {
                        return BooleanUtils.toBoolean(textValue);
                    }
                } else {// number
                    return BooleanUtils.toBoolean(jsonNode.intValue());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get boolean error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return byte[], 默认为 null
     */
    public static byte[] getAsBytes(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return null;
            }
            return jsonNode.isBinary() ? jsonNode.binaryValue() : getAsString(jsonNode).getBytes();
        } catch (Exception e) {
            throw new RuntimeException(String.format("jackson get byte error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return object, 默认为 null
     */
    public static <V> V getAsObject(String json, String key, Class<V> type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return null;
            }
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return from(getAsString(jsonNode), javaType);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("jackson get list error, json: %s, key: %s, type: %s", json, key, type.toString()),
                    e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return list, 默认为 null
     */
    public static <V> List<V> getAsList(String json, String key, Class<V> type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return null;
            }
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return from(getAsString(jsonNode), collectionType);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("jackson get list error, json: %s, key: %s, type: %s", json, key, type.toString()),
                    e);
        }
    }

    /**
     * 从json串中获取某个字段
     * 
     * @return JsonNode, 默认为 null
     */
    public static JsonNode getAsJsonObject(String json, String key) {
        try {
            JsonNode node = mapper.readTree(json);
            if (null == node) {
                return null;
            }
            return node.get(key);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson get object from json error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 向json中添加属性
     * 
     * @return json
     */
    public static <V> String add(String json, String key, V value) {
        try {
            JsonNode node = mapper.readTree(json);
            add(node, key, value);
            return node.toString();
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson add error, json: %s, key: %s, value: %s", json, key, value.toString()), e);
        }
    }

    /**
     * 除去json中的某个属性
     * 
     * @return json
     */
    public static String remove(String json, String key) {
        try {
            JsonNode node = mapper.readTree(json);
            ((ObjectNode) node).remove(key);
            return node.toString();
        } catch (IOException e) {
            throw new RuntimeException(String.format("jackson remove error, json: %s, key: %s", json, key), e);
        }
    }

    /**
     * 修改json中的属性
     */
    public static <V> String update(String json, String key, V value) {
        try {
            JsonNode node = mapper.readTree(json);
            ((ObjectNode) node).remove(key);
            add(node, key, value);
            return node.toString();
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("jackson update error, json: %s, key: %s, value: %s", json, key, value.toString()),
                    e);
        }
    }

    /**
     * 修改指定属性的名称
     * 
     * @param jsonNode -- json对象
     * @param jsonPath -- 包含指定属性的父节点路径，格式参见：https://github.com/json-path/JsonPath
     * @param oldKeyName -- 指定的属性名称
     * @param newKeyName -- 新属性名称
     * @return
     */
    public static JsonNode renameJsonObjectKey( //
            JsonNode jsonNode, String jsonPath, String oldKeyName, String newKeyName) {
        return JsonUtils.jsonParseContext.parse(jsonNode).renameKey(jsonPath, oldKeyName, newKeyName).json();
    }

    /**
     * 修改指定属性的名称
     * 
     * @param jsonNode -- json字符串
     * @param jsonPath -- 包含指定属性的父节点路径，格式参见：https://github.com/json-path/JsonPath
     * @param oldKeyName -- 指定的属性名称
     * @param newKeyName -- 新属性名称
     * @return
     */
    public static JsonNode renameJsonObjectKey( //
            String json, String jsonPath, String oldKeyName, String newKeyName) {
        return JsonUtils.renameJsonObjectKey(JsonUtils.from(json, JsonNode.class), jsonPath, oldKeyName, newKeyName);
    }

    /**
     * 删除指定节点
     * 
     * @param jsonNode -- json
     * @param jsonPath -- 包含指定属性的父节点路径，格式参见：https://github.com/json-path/JsonPath
     * @return
     */
    public static JsonNode deleteJsonObjectKey(JsonNode jsonNode, String jsonPath) {
        return (JsonNode) jsonParseContext.parse(jsonNode).delete(jsonPath).json();
    }

    /**
     * 删除指定节点
     * 
     * @param json -- json字符串
     * @param jsonPath -- 包含指定属性的父节点路径，格式参见：https://github.com/json-path/JsonPath
     * @return
     */
    public static JsonNode deleteJsonObjectKey(String json, String jsonPath) {
        return deleteJsonObjectKey((JsonNode) from(json, JsonNode.class), jsonPath);
    }

    /**
     * 增加属性
     * 
     * @param jsonNode -- json
     * @param jsonPath -- 包含指定属性的父节点路径，格式参见：https://github.com/json-path/JsonPath
     * @param key -- 新增的属性名称
     * @param value -- 新增的属性的value
     * @return
     */
    public static JsonNode addJsonObjectKey(JsonNode jsonNode, String jsonPath, String key, Object value) {
        return (JsonNode) jsonParseContext.parse(jsonNode).put(jsonPath, key, value).json();
    }

    /**
     * 增加属性
     * 
     * @param json -- json字符串
     * @param jsonPath -- 包含指定属性的父节点路径，格式参见：https://github.com/json-path/JsonPath
     * @param key -- 新增的属性名称
     * @param value -- 新增的属性的value
     * @return
     */
    public static JsonNode addJsonObjectKey(String json, String jsonPath, String key, Object value) {
        return addJsonObjectKey((JsonNode) from(json, JsonNode.class), jsonPath, key, value);
    }

    // #endregion

    // #region[Methods] -- Others
    /**
     * 格式化Json(美化)
     *
     * @param json json格式的字符串
     * @return json格式化
     */
    public static String format(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (IOException e) {
            throw new RuntimeException(String.format("jackson format json error, json: %s", json), e);
        }
    }

    /**
     * 判断字符串是否是json
     *
     * @param json json格式的字符串
     * @return 是否是json
     */
    public static boolean isJson(String json) {
        try {
            mapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 
     * @param obj
     * @return
     */
    public static JsonNode convertObjectToJsonNode(Object obj) {
        return mapper.valueToTree(obj);
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }
    // #endregion

    // #region[Helps]

    private static String getAsString(JsonNode jsonNode) {
        return jsonNode.isTextual() ? jsonNode.textValue() : jsonNode.toString();
    }

    /**
     * 向json中添加属性
     */
    private static <V> void add(JsonNode jsonNode, String key, V value) {
        if (value instanceof String) {
            ((ObjectNode) jsonNode).put(key, (String) value);
        } else if (value instanceof Short) {
            ((ObjectNode) jsonNode).put(key, (Short) value);
        } else if (value instanceof Integer) {
            ((ObjectNode) jsonNode).put(key, (Integer) value);
        } else if (value instanceof Long) {
            ((ObjectNode) jsonNode).put(key, (Long) value);
        } else if (value instanceof Float) {
            ((ObjectNode) jsonNode).put(key, (Float) value);
        } else if (value instanceof Double) {
            ((ObjectNode) jsonNode).put(key, (Double) value);
        } else if (value instanceof BigDecimal) {
            ((ObjectNode) jsonNode).put(key, (BigDecimal) value);
        } else if (value instanceof BigInteger) {
            ((ObjectNode) jsonNode).put(key, (BigInteger) value);
        } else if (value instanceof Boolean) {
            ((ObjectNode) jsonNode).put(key, (Boolean) value);
        } else if (value instanceof byte[]) {
            ((ObjectNode) jsonNode).put(key, (byte[]) value);
        } else {
            ((ObjectNode) jsonNode).put(key, to(value));
        }
    }
    // #endregion
}
