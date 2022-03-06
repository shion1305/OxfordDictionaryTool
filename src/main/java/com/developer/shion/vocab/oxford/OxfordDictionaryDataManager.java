package com.developer.shion.vocab.oxford;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class OxfordDictionaryDataManager {
    private static final String tableName = "OxfordApiResponse";
    private static final String PARAM_KEYWORD = "KEYWORD";
    private static final String PARAM_RESP = "RESPONSE";
    String url = "jdbc:sqlite:" + new File("databases/DatabaseForVocabTools.db").getAbsolutePath();

    public OxfordDictionaryDataManager() {
        init();
    }

    public void INSERT(String keyword, String response, Statement statement) throws SQLException {
        String sql = "INSERT INTO " + tableName + "(" + PARAM_KEYWORD + ", " + PARAM_RESP + ") VALUES('" + keyword + "','" + response + "')";
        statement.execute(sql);
    }

    public ArrayList<String> getHistory() throws SQLException {
        ArrayList<String> resp = new ArrayList<String>();
        try (Statement statement = createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " ORDER BY id DESC");
            while (resultSet.next()) {
                resp.add(resultSet.getString(PARAM_KEYWORD));
            }
        }
        return resp;
    }

    private Statement createStatement() throws SQLException {
        return DriverManager.getConnection(url).createStatement();
    }

    public void addData(String keyword, String response, Statement statement) throws SQLException {
        OxfordApiData search = this.SELECT(keyword, statement);
        if (search == null) {
            INSERT(keyword, response.replace("'", "#$#$"), statement);
        } else {
            if (!search.getResponse().equals(response)) {
                UPDATE(keyword, response, statement);
            }
        }
    }

    public void UPDATE(String keyword, String response, Statement statement) throws SQLException {
        String sql = "UPDATE " + tableName + " set " + PARAM_RESP + "='" + response + "' where " + PARAM_KEYWORD + "='" + keyword + "';";
        statement.execute(sql);
    }

    public OxfordApiData SELECT(String keyword, Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + PARAM_KEYWORD + " = '" + keyword + "'");
        if (resultSet.next()) {
            return new OxfordApiData(resultSet.getString(PARAM_KEYWORD), resultSet.getString(PARAM_RESP).replace("#$#$", "'"));
        } else {
            return null;
        }
    }


    public OxfordApiData search(String keyword) throws SQLException, IOException {
        OxfordApiData search;
        try (Statement statement = createStatement()) {
            search = SELECT(keyword, statement);
            if (search == null) {
                search = accessApi(keyword);
                if (search == null) return null;
                addData(search.getKeyword(), search.getResponse(), statement);
            }
        }
        return search;
    }

    public void init() {
        File file = new File("databases/DatabaseForVocabTools.db");
        String url = "jdbc:sqlite:" + file.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(url);
             Statement statement = conn.createStatement()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("A new database has been created.");
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + PARAM_KEYWORD + " text NOT NULL,\n"
                    + PARAM_RESP + " text NOT NULL \n"
                    + ");";
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public OxfordApiData accessApi(String keyword) throws IOException {
        String language = "en-us";
        String appId = InternalApiKeyManager.getApiId();
        String appKey = InternalApiKeyManager.getApiKey();
        String s = "https://od-api.oxforddictionaries.com/api/v2/entries/" + language + "/" + keyword;
        HttpURLConnection connection = ((HttpURLConnection) new URL(s).openConnection());
        connection.setRequestMethod("GET");
        connection.addRequestProperty("app_id", appId);
        connection.addRequestProperty("app_key", appKey);
        connection.addRequestProperty("Accept-Charset", "UTF-8");
        connection.connect();
        if (connection.getResponseCode() < 300) {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
            BufferedReader reader1 = new BufferedReader(reader);
            StringBuilder response = new StringBuilder();
            String tmp;
            while ((tmp = reader1.readLine()) != null) {
                response.append(tmp);
            }
            return new OxfordApiData(keyword, response.toString().replace("    ", ""));
        } else {
            System.out.println(connection.getResponseCode());
            return null;
        }
    }
}
