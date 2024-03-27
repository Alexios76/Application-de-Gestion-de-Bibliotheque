import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connexion {
    private Connection cn; // Déclaration de la connexion comme variable de classe

    public Connection nouvelleConnexion() {
        String url = "jdbc:mysql://localhost/bibliotheque";
        String login = "root";
        String passwd = "";

        try {
            // Chargement du driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Récupération de la connexion et retour
            cn = DriverManager.getConnection(url, login, passwd);
            return cn;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // En cas d'erreur, retourne null
        }
    }

    public ResultSet query(String sql) throws SQLException {
        Statement st = null;
        ResultSet rs = null;

        try {
            // Création d'un statement
            st = cn.createStatement();

            // Exécution de la requête
            rs = st.executeQuery(sql);
            return rs;

        } catch (SQLException e) {
            // Gestion de l'exception dans la classe appelante
            throw e;
        }
    }
}
