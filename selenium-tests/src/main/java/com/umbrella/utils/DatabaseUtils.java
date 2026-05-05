package com.umbrella.utils;

import com.umbrella.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Database Utils - Công cụ tương tác với Database Larango
 * 
 * Mục đích: Thực hiện các thao tác với database để verify dữ liệu hoặc prepare test data
 * Bao gồm: Connect, Query, Insert, Update, Delete, Cleanup
 * 
 * Database: Larango (MySQL-compatible)
 */
public class DatabaseUtils {
    
    private static final Logger logger = LogManager.getLogger(DatabaseUtils.class);
    
    private static Connection connection;
    
    /**
     * Kết nối đến database
     * 
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String dbDriver = ConfigManager.getDbDriver();
                String dbHost = ConfigManager.getDbHost();
                String dbPort = ConfigManager.getDbPort();
                String dbName = ConfigManager.getDbName();
                String dbUser = ConfigManager.getDbUser();
                String dbPassword = ConfigManager.getDbPassword();
                
                String connectionUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
                
                logger.info("Kết nối đến database: " + connectionUrl);
                
                Class.forName(dbDriver);
                connection = DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
                
                logger.info("Kết nối database thành công");
            }
        } catch (Exception e) {
            logger.error("Lỗi kết nối database: " + e.getMessage(), e);
            throw new RuntimeException("Không thể kết nối database", e);
        }
        
        return connection;
    }
    
    /**
     * Đóng kết nối database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Đóng kết nối database");
            }
        } catch (SQLException e) {
            logger.error("Lỗi đóng kết nối: " + e.getMessage(), e);
        }
    }
    
    /**
     * Thực thi query SELECT
     * 
     * @param query: SQL query string
     * @return Map containing query results
     */
    public static Map<String, Object> executeQuery(String query) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    resultMap.put(columnName, value);
                }
                
                logger.debug("Query result: " + resultMap);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            logger.error("Lỗi thực thi query: " + e.getMessage(), e);
            throw new RuntimeException("Query failed: " + query, e);
        }
        
        return resultMap;
    }
    
    /**
     * Thực thi INSERT/UPDATE/DELETE
     * 
     * @param query: SQL query string
     * @return Số hàng được ảnh hưởng
     */
    public static int executeUpdate(String query) {
        int rowsAffected = 0;
        
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            rowsAffected = statement.executeUpdate(query);
            statement.close();
            
            logger.info("Query executed. Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            logger.error("Lỗi thực thi update: " + e.getMessage(), e);
            throw new RuntimeException("Update failed: " + query, e);
        }
        
        return rowsAffected;
    }
    
    /**
     * Kiểm tra lịch khám đã được tạo
     * 
     * @param scheduleId: ID của lịch khám
     * @return Boolean
     */
    public static boolean isScheduleExist(String scheduleId) {
        String query = "SELECT 1 FROM schedules WHERE id = '" + scheduleId + "'";
        Map<String, Object> result = executeQuery(query);
        return !result.isEmpty();
    }
    
    /**
     * Lấy thông tin lịch khám
     * 
     * @param scheduleId: ID của lịch khám
     * @return Map chứa thông tin lịch khám
     */
    public static Map<String, Object> getScheduleInfo(String scheduleId) {
        String query = "SELECT * FROM schedules WHERE id = '" + scheduleId + "'";
        return executeQuery(query);
    }
    
    /**
     * Lấy số lượng lịch khám theo ngày
     * 
     * @param date: Ngày (format: yyyy-MM-dd)
     * @return Số lượng lịch khám
     */
    public static int getScheduleCountByDate(String date) {
        String query = "SELECT COUNT(*) as count FROM schedules WHERE DATE(appointment_date) = '" + date + "'";
        Map<String, Object> result = executeQuery(query);
        return ((Number) result.getOrDefault("count", 0)).intValue();
    }
    
    /**
     * Xóa lịch khám để cleanup
     * 
     * @param scheduleId: ID của lịch khám
     * @return Số hàng bị xóa
     */
    public static int deleteSchedule(String scheduleId) {
        String query = "DELETE FROM schedules WHERE id = '" + scheduleId + "'";
        return executeUpdate(query);
    }
    
    /**
     * Clear test data - Xóa tất cả lịch khám của ngày test
     * 
     * @param date: Ngày test (format: yyyy-MM-dd)
     */
    public static void cleanupTestData(String date) {
        logger.info("Cleanup test data cho ngày: " + date);
        String query = "DELETE FROM schedules WHERE DATE(appointment_date) = '" + date + "'";
        int rowsDeleted = executeUpdate(query);
        logger.info("Deleted " + rowsDeleted + " test records");
    }
    
    /**
     * Reset auto increment để chuẩn bị cho test mới
     */
    public static void resetAutoIncrement() {
        logger.info("Reset auto increment");
        String query = "ALTER TABLE schedules AUTO_INCREMENT = 1";
        executeUpdate(query);
    }
}
