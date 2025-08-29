package grilex.grilexnewsplugin.database.Factories;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.database.Repository;
import grilex.grilexnewsplugin.database.mysql.MySqlBookRepository;
import grilex.grilexnewsplugin.database.mysql.MySqlDatabaseConnection;

public class RepositoryFactory {
    private final GrilexNewsPlugin plugin;

    public RepositoryFactory(GrilexNewsPlugin plugin){
        this.plugin = plugin;
    }

    public Repository getRepository(String dbType){
        switch (dbType.toLowerCase()) {
            case ("sqlite"):
                return null;
            //new SqlitePlayerRepository(new SqliteDatabaseConnection(plugin),this.plugin);
            case ("mysql"):
                return new MySqlBookRepository(new MySqlDatabaseConnection(this.plugin));
            default:
                throw new IllegalStateException("Unexpected value: " + dbType);
        }
    }
}
