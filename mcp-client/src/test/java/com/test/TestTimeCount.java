package com.test;

import org.springframework.util.StopWatch;

public class TestTimeCount {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        // long startTime = System.currentTimeMillis();
        stopWatch.start("task 1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println("耗时：" + stopWatch.prettyPrint());
    }
}
