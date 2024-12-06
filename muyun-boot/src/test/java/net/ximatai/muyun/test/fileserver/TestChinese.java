package net.ximatai.muyun.test.fileserver;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import net.ximatai.muyun.test.testcontainers.PostgresTestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@QuarkusTest
@QuarkusTestResource(value = PostgresTestResource.class)
public class TestChinese {
    
    String fileName;
    
    File fileFirst;
    
    @Test
    @DisplayName("测试文件的创建、写入和删除操作")
    public void test() throws IOException {
        fileName = "五月天.txt";
        Path filePath = Paths.get("./" + fileName);
        List<String> lines = Arrays.asList(
            "我不愿让你一个人",
            "一个人在人海浮沉",
            "我不愿你独自走过风雨的时分"
        );
        Files.write(filePath, lines, StandardCharsets.UTF_8);
        fileFirst = filePath.toFile();
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println(currentTime);
        fileFirst.delete();
    }
}
