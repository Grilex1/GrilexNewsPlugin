package grilex.grilexnewsplugin.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {
    Connection getConnection() throws SQLException;
    void createTable() throws SQLException;
    void closeConnection();
}
