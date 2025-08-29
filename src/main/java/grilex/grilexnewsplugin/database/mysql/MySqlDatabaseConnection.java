package grilex.grilexnewsplugin.database.mysql;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlDatabaseConnection implements DatabaseConnection {
    private final GrilexNewsPlugin plugin;
    private Connection connection;
    private String host;
    private String port;
    private String database;
    private String url;
    private String user;
    private String password;

    public MySqlDatabaseConnection(GrilexNewsPlugin plugin){
        this.plugin = plugin;
        this.port = plugin.getConfig().getString("database.port");
        this.database = plugin.getConfig().getString("database.database");
        this.user = this.plugin.getConfig().getString("database.user");
        this.password = this.plugin.getConfig().getString("database.password");
        this.host = this.plugin.getConfig().getString("database.host");
        this.url ="jdbc:mysql://" + this.host + ":" + port + "/" + database ;
    }

    @Override
    public Connection getConnection() throws SQLException {
        this.connection = DriverManager.getConnection(this.url,this.user,this.password);
        return this.connection;
    }

    @Override
    public void createTable() throws SQLException {
        String sqlClean = "CREATE TABLE IF NOT EXISTS clean ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text TEXT"
                + ")";
        String sqlDrafts = "CREATE TABLE IF NOT EXISTS drafts ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text TEXT"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlClean);
            stmt.execute(sqlDrafts);
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
