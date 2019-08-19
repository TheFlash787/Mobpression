package net.modrealms.mobpression.data;

import net.modrealms.mobpression.Mobpression;
import org.sqlite.JDBC;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DatabaseHandler {
    private static final String path = "jdbc:sqlite:" + Mobpression.getInstance().getConfigDir().toURI().getPath() + "storage.db";
    private Connection connection;
    public DatabaseHandler() {
        try {
            createIfNotExists();
            new JDBC();
            connection = DriverManager.getConnection(path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createNewTable();
    }

    private void createNewTable() {
        /* Making a new table */
        String sql = "CREATE TABLE IF NOT EXISTS entities (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	uuid varchar NOT NULL,\n"
                + "	amount integer NOT NULL \n"
                + ");";

        try {
            Statement stmt = connection.createStatement();
            /* Execute the statement */
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateEntity(UUID entityId, int amount){
        String sql = "INSERT OR REPLACE INTO entities(uuid, amount) VALUES(?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entityId.toString());
            pstmt.setInt(2, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteEntity(UUID entityId){
        String sql = "DELETE from entities WHERE uuid = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entityId.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public HashMap<UUID, Integer> getEntities(){
        String sql = "SELECT * FROM entities";
        HashMap<UUID, Integer> entities = new HashMap<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                entities.put(UUID.fromString(rs.getString("uuid")), rs.getInt("amount"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return entities;
    }

    public void close(){
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createIfNotExists() {
        Path configPath = Mobpression.getInstance().getConfigDir().toPath();
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!Files.exists(Paths.get(configPath.toUri().getPath() + "storage.db"))){
            try {
                Files.copy(Mobpression.class.getResourceAsStream("storage.db"), Paths.get(configPath.toUri().getPath() + "storage.db"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
