package io.gamioo.sandbox;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.protobuf.util.JsonFormat;
import io.gamioo.sandbox.fbs.Harm;
import io.gamioo.sandbox.proto.Skill;
import io.gamioo.sandbox.util.FileUtils;
import io.gamioo.sandbox.util.MathUtils;
import org.apache.fory.Fory;
import org.apache.fory.config.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.openjdk.jol.info.GraphLayout;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


@DisplayName("proto test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProtoTest {
    private static final Logger logger = LogManager.getLogger(ProtoTest.class);


    private static SkillFire_S2C_Msg skillFire_s2C_msg;


    private static Skill.SkillFire_S2C_Msg skillFire_s2C_msg_proto;

    private static io.gamioo.sandbox.fbs.SkillFire_S2C_Msg skillFire_s2C_msg_fbs;

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
        logger.info("raw java bean size:{}", size);
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
            logger.info("protobuf java bean size:{}", size);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        {
            try {
                byte[] array = FileUtils.getByteArrayFromFile("message.bin");
                // 1. 将字节数组包装成 ByteBuffer
                ByteBuffer buffer = ByteBuffer.wrap(array);
                skillFire_s2C_msg_fbs = io.gamioo.sandbox.fbs.SkillFire_S2C_Msg.getRootAsSkillFire_S2C_Msg(buffer);
                size = GraphLayout.parseInstance(skillFire_s2C_msg_fbs).totalSize();
                logger.info("flatBuffers java bean size:{}", size);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }


        }


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
        logger.info("Json2 Serializable {},{}", bytes.length,MathUtils.prettyPercentage((double) bytes.length / size));
    }


    @DisplayName("Json2 Serializable with BeanToArray")
    @Test
    @Order(2)
    public void handleJson2WithBeanToArraySerialize() {
        bytes = JSONB.toBytes(skillFire_s2C_msg, JSONWriter.Feature.BeanToArray);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Json2 Serializable  with BeanToArray {},{}" ,bytes.length, MathUtils.prettyPercentage((double) bytes.length / size));
    }


    @DisplayName("Json2 Deserialize with SupportArrayToBean")
    @Test
    @Order(3)
    public void handleJson2Deserialize() {
        JSONB.parseObject(bytes, SkillFire_S2C_Msg.class, JSONReader.Feature.SupportArrayToBean);
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
    @Order(4)
    public void handleFurySerialize() {
        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Fury Serializable {},{}" ,bytes.length,MathUtils.prettyPercentage((double) bytes.length / size));
    }

    @DisplayName("Fury Serializable with Number Compress")
    @Test
    @Order(5)
    public void handleFurySerializeWithCompress() {
        fury = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(true).build();

        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        //  logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger.info("Fury Serializable with Number Compress {},{}" ,bytes.length, MathUtils.prettyPercentage((double) bytes.length / size));
    }


    @DisplayName("Fury Serializable with Number Compress and class register")
    @Test
    @Order(6)
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
        logger.info("Fury Serializable with Number Compress and class register {},{}" ,bytes.length,MathUtils.prettyPercentage((double) bytes.length / size));
    }


    @DisplayName("Fury Deserialize")
    @Test
    @Order(7)
    public void handleFuryDeserialize() {
        // logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
    }

    @DisplayName("Protostuff Serializable")
    @Test
    @Order(8)
    public void handleProtostuffSerialize() {
        bytes = SerializingUtil.serialize(skillFire_s2C_msg);
        long size = GraphLayout.parseInstance(skillFire_s2C_msg).totalSize();
        logger.info("Protostuff Serializable {},{} " ,  bytes.length ,MathUtils.prettyPercentage((double) bytes.length / size));
    }

    @DisplayName("Protostuff Deserialize")
    @Test
    @Order(9)
    public void handleProtostuffDeserialize() {
        logger.info(SerializingUtil.deserialize(bytes, SkillFire_S2C_Msg.class));
    }


    @DisplayName("Protobuf Serializable")
    @Test
    @Order(10)
    public void handleProtobufSerialize() {
        bytes = skillFire_s2C_msg_proto.toByteArray();
        long size = GraphLayout.parseInstance(skillFire_s2C_msg_proto).totalSize();
        logger.info("Protobuf Serializable {} ,{} " ,bytes.length,MathUtils.prettyPercentage((double) bytes.length / size));
    }

    @DisplayName("Protobuf Deserialize")
    @Test
    @Order(11)
    public void handleProtobufDeserialize() {
        try {
            // 反序列化Protobuf对象
            Skill.SkillFire_S2C_Msg deserialized = Skill.SkillFire_S2C_Msg.parseFrom(bytes);
       //     logger.info("Protobuf Deserialize success: " + deserialized);
        } catch (Exception e) {
            logger.error("Protobuf Deserialize failed: " + e.getMessage(), e);
        }
    }


    @DisplayName("FlatBuffers Serializable")
    @Test
    @Order(12)
    public void handleFlatBuffersSerialize() {
        ByteBuffer buffer = skillFire_s2C_msg_fbs.getByteBuffer();
        //      long attackerId = skillFire_s2C_msg_fbs.attackerId();

//        Harm.Vector vector=skillFire_s2C_msg_fbs.harmListVector();
//        int size = vector.length();
//        for (int i = 0; i < size; i++) {
//            io.gamioo.sandbox.fbs.Harm harm = vector.get(i);
//
//            // 可以在这里处理 harm 对象的数据
//        }
        // 将 ByteBuffer 转换为字节数组
        bytes = buffer.array();
        long size = GraphLayout.parseInstance(skillFire_s2C_msg_fbs).totalSize();
        logger.info("FlatBuffers Serializable byte:{}, {}",bytes.length,MathUtils.prettyPercentage((double) bytes.length / size));
    }

    @DisplayName("FlatBuffers Deserialize")
    @Test
    @Order(13)
    public void handleFlatBuffersDeserialize() {
        // 反序列化Protobuf对象
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        io.gamioo.sandbox.fbs.SkillFire_S2C_Msg.getRootAsSkillFire_S2C_Msg(buffer);
        logger.info("FlatBuffers Deserialize success: ");
    }

    @Test
    @DisplayName("手动填充 FlatBuffers 对象")
    @Order(14)
    public void handleManualFlatBuffersBuild() {
        // 创建 FlatBuffers Builder
        FlatBufferBuilder builder = new FlatBufferBuilder();

        // 创建 harmList 数据
        int harm1 = Harm.createHarm(builder, 1001L, 100.0, 50.0, 0, (byte) 0, 0, false);
        int harm2 = Harm.createHarm(builder, 1002L, 200.0, 75.0, 0, (byte) 0, 0, false);

        // 创建 harmList 向量
        int harmListOffset =  io.gamioo.sandbox.fbs.SkillFire_S2C_Msg.createHarmListVector(builder, new int[]{harm1, harm2});

        // 创建 SkillFire_S2C_Msg 对象
        int skillFireOffset =  io.gamioo.sandbox.fbs.SkillFire_S2C_Msg.createSkillFire_S2C_Msg(builder,
                12345L,     // attackerId
                (byte) 1212,     // index
                100,        // skillId
                50,         // skillLevel
                harmListOffset  // harmList
        );

        // 完成构建
        builder.finish(skillFireOffset);

        // 获取构建好的 SkillFire_S2C_Msg
        ByteBuffer buffer = builder.dataBuffer();

                io.gamioo.sandbox.fbs.SkillFire_S2C_Msg.getRootAsSkillFire_S2C_Msg(buffer);

    }



}
