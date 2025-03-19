package com.example.demotrade.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class StoredProcedureUtil {
    private static final Logger logger = LoggerFactory.getLogger(StoredProcedureUtil.class);

    /**
     * 执行存储过程并返回结果
     *
     * @param connection 数据库连接
     * @param procedureName 存储过程名称
     * @param inParams 输入参数列表
     * @param outParamTypes 输出参数类型列表
     * @return 存储过程执行结果
     * @throws SQLException SQL异常
     */
    public List<JSONObject> executeStoredProcedure(Connection connection,
                                                  String procedureName,
                                                  List<Object> inParams,
                                                  List<Integer> outParamTypes) throws SQLException {
        List<JSONObject> result = new ArrayList<>();
        
        // 构建存储过程调用SQL
        StringBuilder sql = new StringBuilder("{call " + procedureName + "(");
        int totalParams = (inParams != null ? inParams.size() : 0) + (outParamTypes != null ? outParamTypes.size() : 0);
        for (int i = 0; i < totalParams; i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")}");

        try (CallableStatement stmt = connection.prepareCall(sql.toString())) {
            // 设置输入参数
            int paramIndex = 1;
            if (inParams != null) {
                for (Object param : inParams) {
                    stmt.setObject(paramIndex++, param);
                }
            }

            // 注册输出参数
            if (outParamTypes != null) {
                for (Integer paramType : outParamTypes) {
                    stmt.registerOutParameter(paramIndex++, paramType);
                }
            }

            // 执行存储过程
            boolean hasResults = stmt.execute();

            // 获取输出参数值
            if (outParamTypes != null) {
                JSONObject outParams = new JSONObject();
                paramIndex = (inParams != null ? inParams.size() : 0) + 1;
                for (int i = 0; i < outParamTypes.size(); i++) {
                    outParams.put("param_" + i, stmt.getObject(paramIndex++));
                }
                if (!outParams.isEmpty()) {
                    result.add(outParams);
                }
            }

            // 处理所有结果集
            while (hasResults) {
                ResultSet rs = stmt.getResultSet();
                if (rs != null) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (rs.next()) {
                        JSONObject row = new JSONObject();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value);
                        }
                        result.add(row);
                    }
                }
                hasResults = stmt.getMoreResults();
            }

        } catch (SQLException e) {
            logger.error("执行存储过程失败: {}", e.getMessage(), e);
            throw e;
        }

        return result;
    }
}