package com.rz.bookapi.dao;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Basic Database Connecion Pooling using Apache Commons DBCP
 */
public class DBCPDataSource {
    private static final BasicDataSource ds;
    // initialise driver and datasource
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }

        ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/zoioraph");
        ds.setUsername("zoioraph");
        ds.setPassword("hertHopl9");
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
    }

    // static method to return datasource connection
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private DBCPDataSource(){ }
}