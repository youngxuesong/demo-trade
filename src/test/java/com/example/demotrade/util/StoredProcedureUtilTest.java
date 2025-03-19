package com.example.demotrade.util;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StoredProcedureUtilTest {

    @Autowired
    private StoredProcedureUtil storedProcedureUtil;

    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // 创建H2内存数据库
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:test-schema.sql")
                .build();
    }

    @Test
    void testExecuteStoredProcedure() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // 准备测试数据
            List<Object> inParams = Arrays.asList("测试用户", 25);
            List<Integer> outParamTypes = Arrays.asList(Types.VARCHAR);

            // 执行存储过程
            List<JSONObject> result = storedProcedureUtil.executeStoredProcedure(
                    connection,
                    "GET_USER_INFO",
                    inParams,
                    outParamTypes
            );

            // 验证结果
            assertNotNull(result);
            assertFalse(result.isEmpty());

            // 验证输出参数
            JSONObject outParam = result.get(0);
            assertNotNull(outParam.get("param_0"));

            // 验证结果集
            if (result.size() > 1) {
                JSONObject resultSet = result.get(1);
                assertEquals("测试用户", resultSet.getString("name"));
                assertEquals(25, resultSet.getIntValue("age"));
            }
        }
    }
}