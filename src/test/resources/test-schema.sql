-- 创建用户信息表
CREATE TABLE IF NOT EXISTS user_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    age INT
);

-- 创建测试用的存储过程
CREATE ALIAS GET_USER_INFO AS $$
String getUserInfo(Connection conn, String name, Integer age) throws SQLException {
    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_info (name, age) VALUES (?, ?)")) {
        stmt.setString(1, name);
        stmt.setInt(2, age);
        stmt.executeUpdate();
    }
    return "SUCCESS";
}
$$;