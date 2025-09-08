package io.gamioo.sandbox;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONWriter;
import com.github.houbb.data.factory.core.util.DataUtil;
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

import java.nio.ByteBuffer;
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
public class ProtoSerializeBenchMark {
    private static final Logger logger = LogManager.getLogger(ProtoSerializeBenchMark.class);
    private SkillFire_S2C_Msg skillFire_s2c_msg = DataUtil.build(SkillFire_S2C_Msg.class);

    private static Skill.SkillFire_S2C_Msg skillFire_s2c_msg_proto;

    private static io.gamioo.sandbox.fbs.SkillFire_S2C_Msg skillFire_s2c_msg_fbs;

    private Fory fury;
    private Fory furyX;

    @Setup
    public void init() {
        JSONFactory.setDisableAutoType(true);
        JSONFactory.setDisableReferenceDetect(true);

        try {
            byte[] array = FileUtils.getByteArrayFromFile("message.txt");
            skillFire_s2c_msg = JSON.parseObject(array, SkillFire_S2C_Msg.class);
            logger.info(skillFire_s2c_msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        fury = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).build();

        furyX = Fory.builder().withLanguage(Language.JAVA)
                .withRefTracking(false).requireClassRegistration(true).withNumberCompressed(true).build();
        furyX.register(SkillFire_S2C_Msg.class);
        furyX.register(SkillCategory.class);
        furyX.register(HarmDTO.class);

        logger.debug("size:{}", GraphLayout.parseInstance(skillFire_s2c_msg).totalSize());

        {
            try {
                // 读取message.txt中的JSON数据
                byte[] array = FileUtils.getByteArrayFromFile("message.txt");
                String jsonString = new String(array, StandardCharsets.UTF_8);

                // 使用Protobuf的JsonFormat解析器
                Skill.SkillFire_S2C_Msg.Builder builder = Skill.SkillFire_S2C_Msg.newBuilder();
                JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder);
                skillFire_s2c_msg_proto = builder.build();
                long size = GraphLayout.parseInstance(skillFire_s2c_msg_proto).totalSize();
                logger.info("protobuf size:{}", size);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }


        {
            try {
                byte[] array = FileUtils.getByteArrayFromFile("message.bin");
                // 1. 将字节数组包装成 ByteBuffer
                ByteBuffer buffer = ByteBuffer.wrap(array);
                skillFire_s2c_msg_fbs = io.gamioo.sandbox.fbs.SkillFire_S2C_Msg.getRootAsSkillFire_S2C_Msg(buffer);
                // 将 ByteBuffer 转换为字节数组

                long size = GraphLayout.parseInstance(skillFire_s2c_msg_fbs).totalSize();
                logger.info("flatBuffers java bean size:{}", size);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    @Benchmark
    public void furySerialize(Blackhole blackhole) {
        byte[] result = fury.serialize(skillFire_s2c_msg);
        blackhole.consume(result);
    }

    @Benchmark
    public void furySerializeWithClassRegistrationAndNumberCompressed(Blackhole blackhole) {
        byte[] result = furyX.serialize(skillFire_s2c_msg);
        blackhole.consume(result);
    }

    @Benchmark
    public void jsonSerialize(Blackhole blackhole) {
        byte[] result = JSONB.toBytes(skillFire_s2c_msg);
        blackhole.consume(result);
    }

    @Benchmark
    public void jsonSerializeWithBeanToArray(Blackhole blackhole) {
        byte[] result = JSONB.toBytes(skillFire_s2c_msg, JSONWriter.Feature.BeanToArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void jsonSerializeWithBeanToArrayAndFieldBase(Blackhole blackhole) {
        byte[] result = JSONB.toBytes(skillFire_s2c_msg, JSONWriter.Feature.BeanToArray, JSONWriter.Feature.FieldBased);
        blackhole.consume(result);
    }

    @Benchmark
    public void protostuffSerialize(Blackhole blackhole) {
        byte[] result = SerializingUtil.serialize(skillFire_s2c_msg);
        blackhole.consume(result);
    }

    @Benchmark
    public void protobufSerialize(Blackhole blackhole) {
        byte[] result = skillFire_s2c_msg_proto.toByteArray();
        blackhole.consume(result);
    }

    @Benchmark
    public void flatBuffersSerialize(Blackhole blackhole) {
        ByteBuffer buffer = skillFire_s2c_msg_fbs.getByteBuffer();
        // 将 ByteBuffer 转换为字节数组
        byte[]  bytes = buffer.array();
        blackhole.consume(bytes);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProtoSerializeBenchMark.class.getSimpleName()).result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }
}
