package io.gamioo.sandbox;


import io.netty.util.internal.PlatformDependent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 内存统计
 *
 * @author Allen Jiang
 */
public class MemoryService {
    private static final Logger LOGGER = LogManager.getLogger(MemoryService.class);
    private static MemoryService instance;

    public static MemoryService getInstance() {
        if (null == instance) {
            instance = new MemoryService();
        }
        return instance;
    }

    /**
     * 60s后启动
     */
    private static final int TIME_DELAY = 60000;
    private static final int TIME_INTERVAL = 60000;
    private static final int BYTES_1MB = 1024 * 1024;
    private BufferPoolMXBean directBufferPoolMXBean;
    private String heapUsageName = "";
    private final List<MemoryPoolMXBean> heapUsageList = new ArrayList<>();
    private String nonHeapUsageName = "";
    private final List<MemoryPoolMXBean> nonHeapUsageList = new ArrayList<>();


    // private ByteBuffer buffer;


    public void init() {
        //  buffer = ByteBuffer.allocateDirect(80 * 1024 * 1024);
        directBufferPoolMXBean = this.initDirectBufferPoolMBean();
        this.initMemoryPoolMXBeanList();

    }

    private BufferPoolMXBean initDirectBufferPoolMBean() {
        BufferPoolMXBean ret = null;
        List<BufferPoolMXBean> list = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean e : list) {
            if ("direct".equals(e.getName())) {
                ret = e;
            }
        }
        return ret;
    }


    private void initMemoryPoolMXBeanList() {
        List<MemoryPoolMXBean> list = ManagementFactory.getPlatformMXBeans(MemoryPoolMXBean.class);
        for (MemoryPoolMXBean e : list) {
            if (MemoryType.NON_HEAP == e.getType()) {
                nonHeapUsageList.add(e);
            } else {
                heapUsageList.add(e);
            }
        }
        nonHeapUsageList.sort(Comparator.comparing(MemoryPoolMXBean::getName));
        nonHeapUsageList.forEach(memory -> nonHeapUsageName += memory.getName() + ",");


        heapUsageList.sort(Comparator.comparingInt(memory -> memory.getName().length() % 12));
        heapUsageList.forEach(memory -> heapUsageName += memory.getName() + ",");


        heapUsageName = heapUsageName.substring(0, heapUsageName.length() - 1);
        nonHeapUsageName = nonHeapUsageName.substring(0, nonHeapUsageName.length() - 1);
    }



    public void printMemoryInfo() {
          /*
                  java process memory = java heap memory + native memory(Off-heap memory)
                  native memory= Class(metaSpace)+Thread+Code Cache+GC+Internal+Other(Direct memory)+Symbol
                  ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()=Metaspace+Compressed Class Space+Code Cache
                  Non-heap memory usage, as provided by MemoryPoolMXBean counts the following memory pools:

                  Metaspace
                  Compressed Class Space
                  Code Cache
                  */


            long maxMemoryValue = PlatformDependent.maxDirectMemory();
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
            // Native memory 非堆空间占用信息
            MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
            List<String> heapList = heapUsageList.stream().map(
                    usage -> (usage.getUsage().getUsed() / BYTES_1MB) + "/" + (usage.getUsage().getMax() / BYTES_1MB)
            ).collect(Collectors.toList());
            List<String> nonHeapList = nonHeapUsageList.stream().map(
                    usage -> (usage.getUsage().getUsed() / BYTES_1MB) + "/" + (usage.getUsage().getMax() / BYTES_1MB)
            ).collect(Collectors.toList());
            LOGGER.info("heap memory {} MB/{} MB,[{}]:{},non-heap memory {} MB/{} MB,[{}]:{},direct memory {} MB/{} MB",
                    memoryUsage.getUsed() / BYTES_1MB,
                    memoryUsage.getMax() / BYTES_1MB,
                    heapUsageName,
                    heapList,
                    nonHeapMemoryUsage.getUsed() / BYTES_1MB,
                    nonHeapMemoryUsage.getMax() / BYTES_1MB,
                    nonHeapUsageName,
                    nonHeapList,
                    directBufferPoolMXBean.getMemoryUsed() / BYTES_1MB,
                    maxMemoryValue / BYTES_1MB
            );

        }

}
