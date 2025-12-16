package io.gamioo.sandbox;

import com.github.houbb.data.factory.core.util.DataUtil;
import com.yomahub.roguemap.RogueMap;
import com.yomahub.roguemap.serialization.KryoObjectCodec;
import com.yomahub.roguemap.serialization.PrimitiveCodecs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.openjdk.jol.info.GraphLayout;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@DisplayName("map test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapTest {
    private static final Logger logger = LogManager.getLogger(MapTest.class);
    private static RogueMap<Integer, ItemTemplate> regueStore;
    private static final long RANDOM_SEED = 42;
    private static final int BYTES_1MB = 1024 * 1024;
    private static Map<Integer, ItemTemplate> hashMapStore;

    @BeforeAll
    public static void beforeAll() {
        MemoryService.getInstance().init();
// 场景2: 内存敏感，Long键，推荐原始索引
        regueStore = RogueMap.<Integer, ItemTemplate>mmap()
                .temporary()
                .keyCodec(PrimitiveCodecs.INTEGER)
                .valueCodec(KryoObjectCodec.create(ItemTemplate.class))
                .primitiveIndex()// 节省81%内存da
                .build();

        hashMapStore = new HashMap<>();


    }

    @AfterAll
    public static void afterAll() {
        regueStore.close();
    }

    private static long getCurrentHeapMemory() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    private static long getCurrentNoHeapMemory() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    // ... existing code ...
    @DisplayName("hashmap put")
    @Test
    @Order(1)
    public void putWithHashmap() {
        forceGC();
        long baselineMemory = getCurrentHeapMemory();
        MemoryService.getInstance().printMemoryInfo();

        // 添加10000个元素到HashMap中
        for (int i = 1; i < 1000000; i++) {
            ItemTemplate value = createItemTemplate(i);
            hashMapStore.put(i, value);
        }
        forceGC();
        long usedMemory = getCurrentHeapMemory();
        long heapUsed = usedMemory - baselineMemory;

        logger.info("hashmap heap memory {} MB",heapUsed / BYTES_1MB);
        MemoryService.getInstance().printMemoryInfo();

    }

    private static void forceGC() {
        for (int i = 0; i < 5; i++) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    @DisplayName("rogueMap put")
    @Test
    @Order(2)
    public void putWithRogueMap() {
        forceGC();
        long baselineMemory = getCurrentHeapMemory();
        MemoryService.getInstance().printMemoryInfo();
        Random random = new Random(RANDOM_SEED);

        // 添加10000个元素到RogueMap中
        for (int i = 1; i < 1000000; i++) {
            ItemTemplate value = createItemTemplate(i);
            regueStore.put(i, value);
        }
        forceGC();
        long usedMemory = getCurrentHeapMemory();
        long heapUsed = usedMemory - baselineMemory;
        logger.info("rogueMap heap memory {} MB",heapUsed / BYTES_1MB);
        MemoryService.getInstance().printMemoryInfo();
    }
    private static TestValueObject createTestValue(int i, Random random) {
        return new TestValueObject(
                i,                                          // id
                System.currentTimeMillis() + i,            // timestamp
                99.99 + (random.nextDouble() * 100),       // price
                random.nextBoolean(),                      // active
                "Product_" + i,                            // name
                "Description for product " + i + " with details", // description
                (byte) (random.nextInt(5)),                // status
                (short) (random.nextInt(1000)),            // quantity
                random.nextFloat() * 5,                    // rating
                (char) ('A' + random.nextInt(26))          // category
        );
    }


    private static ItemTemplate createItemTemplate(int i) {
        return  DataUtil.build(ItemTemplate.class);
    }

}
