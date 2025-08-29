package grilex.grilexnewsplugin.database.Factories;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.database.DatabaseConnection;
import grilex.grilexnewsplugin.database.mysql.MySqlDatabaseConnection;

public class DatabaseFactory {
    private final GrilexNewsPlugin plugin;

    public DatabaseFactory(GrilexNewsPlugin plugin){
        this.plugin = plugin;
    }

    public DatabaseConnection getDatabaseConnection(String database) {
        switch (database.toLowerCase()) {
            case ("sqlite"):
                return null;
            //SqliteDatabaseConnection(this.plugin);
            case ("mysql"):
                return new MySqlDatabaseConnection(this.plugin);
            default:
                throw new IllegalStateException("Unexpected value: " + database);
        }

    }
}
