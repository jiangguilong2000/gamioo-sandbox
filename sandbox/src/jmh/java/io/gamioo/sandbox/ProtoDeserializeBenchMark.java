package io.gamioo.sandbox;

import com.alibaba.fastjson2.*;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.houbb.data.factory.core.util.DataUtil;


import io.gamioo.sandbox.util.FileUtils;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.CompilerProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

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
    private Fury fury;

    private Fury furyX;

    private byte[] furyArray;

    private byte[] furyArrayX;

    private byte[] protoArray;

    private byte[] jsonArray;
    private byte[] jsonArrayWithBeanToArray;


    @Setup
    public void init() {
        try {
          byte[]  array = FileUtils.getByteArrayFromFile("message.txt");
            skillFire_s2C_msg= JSON.parseObject(array,SkillFire_S2C_Msg.class);
            logger.info(skillFire_s2C_msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).build();

        furyX = Fury.builder().withLanguage(Language.JAVA)
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
       // String size = RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOf(skillFire_s2C_msg));
        logger.debug("size:{}", RamUsageEstimator.sizeOf(skillFire_s2C_msg));
    }
    @Benchmark
    public SkillFire_S2C_Msg furyDeserialize() {
        return fury.deserializeJavaObject(furyArray, SkillFire_S2C_Msg.class);
    }

    @Benchmark
    public SkillFire_S2C_Msg jsonDeserialize() {
        return JSONB.parseObject(jsonArray, SkillFire_S2C_Msg.class);
    }

    @Benchmark
    public SkillFire_S2C_Msg jsonDeserializeWithArrayToBean() {
        return JSONB.parseObject(jsonArrayWithBeanToArray, SkillFire_S2C_Msg.class, JSONReader.Feature.SupportArrayToBean);
    }


    @Benchmark
    public SkillFire_S2C_Msg jsonDeserializeWithArrayToBeanAndFieldBase() {
        return JSONB.parseObject(jsonArrayWithBeanToArray, SkillFire_S2C_Msg.class, JSONReader.Feature.SupportArrayToBean,JSONReader.Feature.FieldBased);
    }

    @Benchmark
    public SkillFire_S2C_Msg furyDeserializeWithClassRegistrationAndNumberCompressed() {
        return furyX.deserializeJavaObject(furyArrayX, SkillFire_S2C_Msg.class);
    }

    @Benchmark
    public SkillFire_S2C_Msg protostuffDeserialize() {
        return SerializingUtil.deserialize(protoArray, SkillFire_S2C_Msg.class);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProtoDeserializeBenchMark.class.getSimpleName()).result("result.json")
                .resultFormat(ResultFormatType.JSON)
                         //     .addProfiler(GCProfiler.class)
//                .addProfiler(CompilerProfiler.class)
//                .verbosity(VerboseMode.EXTRA)
                .build();
        new Runner(opt).run();
    }
}
