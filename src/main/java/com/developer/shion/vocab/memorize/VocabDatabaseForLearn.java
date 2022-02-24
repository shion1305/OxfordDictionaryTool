package com.developer.shion.vocab.memorize;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Date;

public class VocabDatabaseForLearn {
    private static final String tableName = "VocablaryDatabaseForLearn";
    private Connection conn;
    private static final String PARAM_WORD = "WORD";
    private static final String PARAM_DATE = "RESPONSE";

    public VocabDatabaseForLearn() {
        init();
    }

    public void INSERT(String keyword, String response) throws SQLException {
        String sql = "INSERT INTO " + tableName + "(" + PARAM_WORD + ", " + PARAM_DATE + ") VALUES('" + keyword + "','" + response + "')";
        conn.prepareStatement(sql).executeUpdate();
    }


    public void addData(String word) throws SQLException {
        WordMemorizeData data= this.SELECT(word);
        if (data== null) {
            INSERT(word, String.valueOf(System.currentTimeMillis()));
        }
    }

    public void UPDATE(String keyword) throws SQLException {
        String sql = "UPDATE "+tableName+" set " + PARAM_DATE + "='" + System.currentTimeMillis() + "' where " + PARAM_WORD + "='" + keyword + "';";
        conn.prepareStatement(sql).executeUpdate();
    }

    public WordMemorizeData SELECT(String keyword) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + PARAM_WORD + " = '" + keyword + "'");
        if (resultSet.next()) {
            return new WordMemorizeData(resultSet.getString(PARAM_WORD), resultSet.getString(PARAM_DATE));
        } else {
            return null;
        }
    }


    public WordMemorizeData search(String word) throws SQLException, IOException {
        WordMemorizeData data = SELECT(word);
        if (data == null) {
            addData(word);
        }
        return data;
    }

    public void init() {
        File file = new File("databases/DatabaseForVocabTools.db");
        String url = "jdbc:sqlite:" + file.getAbsolutePath();
        try {
            conn = DriverManager.getConnection(url);
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("A new database has been created.");
            Statement statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + PARAM_WORD + " text NOT NULL,\n"
                    + PARAM_DATE + " text NOT NULL \n"
                    + ");";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
