package io.gamioo.sandbox;

import com.yomahub.roguemap.RogueMap;
import com.yomahub.roguemap.serialization.KryoObjectCodec;
import com.yomahub.roguemap.serialization.PrimitiveCodecs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

@DisplayName("map test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapTest {
    private static final Logger logger = LogManager.getLogger(MapTest.class);
    private static RogueMap<Integer, Point> regueStore;

    private static Map<Integer, Point> hashMapStore;

    @BeforeAll
    public static void beforeAll() {
        MemoryService.getInstance().init();
// 场景2: 内存敏感，Long键，推荐原始索引
        regueStore = RogueMap.<Integer, Point>offHeap()
                .keyCodec(PrimitiveCodecs.INTEGER)
                .valueCodec(KryoObjectCodec.create(Point.class))
                //.primitiveIndex()// 节省81%内存
                .build();

        hashMapStore = new HashMap<>();


    }

    @AfterAll
    public static void afterAll() {

    }

    // ... existing code ...
    @DisplayName("hashmap put")
    @Test
    @Order(1)
    public void putWithHashmap() {
        MemoryService.getInstance().printMemoryInfo();
        // 添加10000个元素到HashMap中
        for (int i = 1; i < 1000000; i++) {
            Point point = new Point();
            point.setX(i);
            point.setY(i * 2);
            hashMapStore.put( i, point);
        }
        MemoryService.getInstance().printMemoryInfo();


    }


    @DisplayName("rogueMap put")
    @Test
    @Order(2)
    public void putWithRogueMap() {
        MemoryService.getInstance().printMemoryInfo();
        // 添加10000个元素到RogueMap中
        for (int i = 1; i < 1000000; i++) {
            Point point = new Point();
            point.setX(i);
            point.setY(i * 2);
            regueStore.put(i, point);
        }
        MemoryService.getInstance().printMemoryInfo();
    }


}
