package io.gamioo.sandbox;

import com.alibaba.fastjson2.*;
import com.github.houbb.data.factory.core.util.DataUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.gamioo.sandbox.proto.Skill;
import io.gamioo.sandbox.util.FileUtils;
import org.apache.fory.Fory;
import org.apache.fory.config.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jol.info.GraphLayout;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author Allen Jiang
 */
@Fork(1)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode({Mode.Throughput})
@State(Scope.Benchmark)
public class ProtoDeserializeBenchMark {
    private static final Logger logger = LogManager.getLogger(ProtoDeserializeBenchMark.class);
    private SkillFire_S2C_Msg  skillFire_s2C_msg = DataUtil.build(SkillFire_S2C_Msg.class);

    private static Skill.SkillFire_S2C_Msg skillFire_s2C_msg_proto;
    private Fory fury;

    private Fory furyX;

    private byte[] furyArray;

    private byte[] furyArrayX;

    private byte[] protoArray;

    private byte[] jsonArray;
    private byte[] jsonArrayWithBeanToArray;

    private byte[] protobufArray;


    @Setup
    public void init() {
        try {
            byte[]  array = FileUtils.getByteArrayFromFile("message.txt");
            skillFire_s2C_msg= JSON.parseObject(array,SkillFire_S2C_Msg.class);
            logger.info(skillFire_s2C_msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        fury = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).build();

        furyX = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(false).requireClassRegistration(false).withNumberCompressed(true).build();
        furyX.register(SkillFire_S2C_Msg.class);
        furyX.register(SkillCategory.class);
        furyX.register(HarmDTO.class);


        furyArray = fury.serializeJavaObject(skillFire_s2C_msg);
        furyArrayX = furyX.serializeJavaObject(skillFire_s2C_msg);
        protoArray = SerializingUtil.serialize(skillFire_s2C_msg);
        JSONFactory.setDisableAutoType(true);
        JSONFactory.setDisableReferenceDetect(true);
        jsonArray = JSONB.toBytes(skillFire_s2C_msg);
        jsonArrayWithBeanToArray =JSONB.toBytes(skillFire_s2C_msg, JSONWriter.Feature.BeanToArray);
        logger.debug("size:{}", GraphLayout.parseInstance(skillFire_s2C_msg).totalSize());

        {
            try {
                // 读取message.txt中的JSON数据
                byte[] array = FileUtils.getByteArrayFromFile("message.txt");
                String jsonString = new String(array, StandardCharsets.UTF_8);

                // 使用Protobuf的JsonFormat解析器
                Skill.SkillFire_S2C_Msg.Builder builder = Skill.SkillFire_S2C_Msg.newBuilder();
                JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder);
                skillFire_s2C_msg_proto = builder.build();
                protobufArray = skillFire_s2C_msg_proto.toByteArray();
                long size = GraphLayout.parseInstance(skillFire_s2C_msg_proto).totalSize();
                logger.info("protobuf size:{}", size);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    @Benchmark
    public void furyDeserialize(Blackhole blackhole) {
        SkillFire_S2C_Msg result = fury.deserializeJavaObject(furyArray, SkillFire_S2C_Msg.class);
        blackhole.consume(result);
    }

    @Benchmark
    public void jsonDeserialize(Blackhole blackhole) {
        SkillFire_S2C_Msg result = JSONB.parseObject(jsonArray, SkillFire_S2C_Msg.class);
        blackhole.consume(result);
    }

    @Benchmark
    public void jsonDeserializeWithArrayToBean(Blackhole blackhole) {
        SkillFire_S2C_Msg result = JSONB.parseObject(jsonArrayWithBeanToArray, SkillFire_S2C_Msg.class, JSONReader.Feature.SupportArrayToBean);
        blackhole.consume(result);
    }

    @Benchmark
    public void jsonDeserializeWithArrayToBeanAndFieldBase(Blackhole blackhole) {
        SkillFire_S2C_Msg result = JSONB.parseObject(jsonArrayWithBeanToArray, SkillFire_S2C_Msg.class, JSONReader.Feature.SupportArrayToBean,JSONReader.Feature.FieldBased);
        blackhole.consume(result);
    }

    @Benchmark
    public void furyDeserializeWithClassRegistrationAndNumberCompressed(Blackhole blackhole) {
        SkillFire_S2C_Msg result = furyX.deserializeJavaObject(furyArrayX, SkillFire_S2C_Msg.class);
        blackhole.consume(result);
    }

    @Benchmark
    public void protostuffDeserialize(Blackhole blackhole) {
        SkillFire_S2C_Msg result = SerializingUtil.deserialize(protoArray, SkillFire_S2C_Msg.class);
        blackhole.consume(result);
    }

    @Benchmark
    public void protobufDeserialize(Blackhole blackhole) throws InvalidProtocolBufferException {
        Skill.SkillFire_S2C_Msg deserialized = Skill.SkillFire_S2C_Msg.parseFrom(protobufArray);
        blackhole.consume(deserialized);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProtoDeserializeBenchMark.class.getSimpleName()).result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }
}
