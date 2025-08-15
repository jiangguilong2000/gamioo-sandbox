package io.gamioo.sandbox;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONWriter;
import com.google.protobuf.util.JsonFormat;
import io.gamioo.sandbox.proto.Skill;
import io.gamioo.sandbox.util.FileUtils;
import io.gamioo.sandbox.util.MathUtils;
import org.apache.fory.Fory;
import org.apache.fory.config.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.openjdk.jol.info.GraphLayout;

import java.nio.charset.StandardCharsets;


@DisplayName("proto test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProtoTest {
    private static final Logger logger = LogManager.getLogger(ProtoTest.class);


    private static SkillFire_S2C_Msg skillFire_s2C_msg;


    private static Skill.SkillFire_S2C_Msg skillFire_s2C_msg_proto;

    private static Fory fury;

    private static byte[] bytes;

    @BeforeAll
    public static void beforeAll() {
        try {
            byte[] array = FileUtils.getByteArrayFromFile("message.txt");
            skillFire_s2C_msg = JSON.parseObject(array, SkillFire_S2C_Msg.class);
            // logger.info(JSON.toJSONString(skillFire_s2C_msg, JSONWriter.Feature.PrettyFormat));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // skillFire_s2C_msg= DataUtil.build(SkillFire_S2C_Msg.class);
        //  logger.info(JSON.toJSONString(skillFire_s2C_msg));
        fury = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(false).build();
        // long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        logger.info("raw size:{}", size);
        try {
            // 读取message.txt中的JSON数据
            byte[] array = FileUtils.getByteArrayFromFile("message.txt");
            String jsonString = new String(array, StandardCharsets.UTF_8);

            // 使用Protobuf的JsonFormat解析器
            Skill.SkillFire_S2C_Msg.Builder builder = Skill.SkillFire_S2C_Msg.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder);
            skillFire_s2C_msg_proto = builder.build();
            size = GraphLayout.parseInstance(skillFire_s2C_msg_proto).totalSize();
            //   logger.info(skillFire_s2C_msg_proto);
            logger.info("protobuf size:{}", size);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


        logger.info(size);
    }

    @AfterAll
    public static void afterAll() {

    }

    @DisplayName("Json2 Serializable")
    @Test
    @Order(1)
    public void handleJson2Serialize() {
        bytes = JSONB.toBytes(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Json2 Serializable " + MathUtils.prettyPercentage((double) bytes.length / size));
    }


    @DisplayName("Json2 Serializable with BeanToArray")
    @Test
    @Order(2)
    public void handleJson2WithBeanToArraySerialize() {
        bytes = JSONB.toBytes(skillFire_s2C_msg, JSONWriter.Feature.BeanToArray);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Json2 Serializable  with BeanToArray " + MathUtils.prettyPercentage((double) bytes.length / size));
    }


//    @DisplayName("ThreadLocalFury Serializable")
//    @Test
//    @Order(3)
//    public void handleThreadLocalFurySerialize() {
//        ThreadLocalFury  fury = Fury.builder().withLanguage(Language.JAVA)
//                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(false).buildThreadLocalFury();
//       bytes = fury.serialize(skillFire_s2C_msg);
//        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
//     //   logger. info(bytes.length);
//     // String size2 = RamUsageEstimator.humanReadableUnits(size);
//     logger. info( "Fury Serializable "+MathUtils.prettyPercentage((double)bytes.length/size));
//    }
//
//    @DisplayName("Fury Deserialize")
//    @Order(8)
//    public void handleThreadLocalFuryDeserialize() {
//        ThreadLocalFury  fury2 = Fury.builder().withLanguage(Language.JAVA)
//                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(false).buildThreadLocalFury();
//
//        logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
//
//        logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
//    }

    @DisplayName("Fury Serializable")
    @Test
    @Order(3)
    public void handleFurySerialize() {
        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Fury Serializable " + MathUtils.prettyPercentage((double) bytes.length / size));
    }

    @DisplayName("Fury Serializable with Number Compress")
    @Test
    @Order(4)
    public void handleFurySerializeWithCompress() {
        fury = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(true).build();

        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //  logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Fury Serializable with Number Compress " + MathUtils.prettyPercentage((double) bytes.length / size));
    }


    @DisplayName("Fury Serializable with Number Compress and class register")
    @Test
    @Order(7)
    public void handleFurySerializeWithCompressAndRegister() {
//.withDeserializeUnExistClassEnabled(true)
        fury = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(true).withNumberCompressed(true).build();
        fury.register(SkillFire_S2C_Msg.class);
        fury.register(SkillCategory.class);
        fury.register(HarmDTO.class);
        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Fury Serializable with Number Compress and class register " + MathUtils.prettyPercentage((double) bytes.length / size));
    }


    @DisplayName("Fury Deserialize")
    @Test
    @Order(8)
    public void handleFuryDeserialize() {
        // logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
    }

    @DisplayName("Protostuff Serializable")
    @Test
    @Order(100)
    public void handleProtostuffSerialize() {
        bytes = SerializingUtil.serialize(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        logger.info("Protostuff Serializable " + MathUtils.prettyPercentage((double) bytes.length / size));
    }

    @DisplayName("Protostuff Deserialize")
    @Test
    @Order(101)
    public void handleProtostuffDeserialize() {
        logger.info(SerializingUtil.deserialize(bytes, SkillFire_S2C_Msg.class));
    }


    @DisplayName("Protobuf Serializable")
    @Test
    @Order(102)
    public void handleProtobufSerialize() {
        bytes = skillFire_s2C_msg_proto.toByteArray();
        long size = GraphLayout.parseInstance(skillFire_s2C_msg_proto).totalSize();
        logger.info("Protobuf Serializable " + MathUtils.prettyPercentage((double) bytes.length / size));
    }

    @DisplayName("Protobuf Deserialize")
    @Test
    @Order(103)
    public void handleProtobufDeserialize() {
        try {
            // 反序列化Protobuf对象
            Skill.SkillFire_S2C_Msg deserialized = Skill.SkillFire_S2C_Msg.parseFrom(bytes);
            logger.info("Protobuf Deserialize success: " + deserialized);
        } catch (Exception e) {
            logger.error("Protobuf Deserialize failed: " + e.getMessage(), e);
        }
    }


}
