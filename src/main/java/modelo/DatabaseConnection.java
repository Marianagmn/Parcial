package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de conexión a la base de datos MySQL.
 * Cada llamada a crearConexion() devuelve una conexión nueva e independiente.
 * Ajusta usuario, clave y BD según tu entorno.
 */
public class DatabaseConnection {

    private static final String USUARIO  = "root";
    private static final String CLAVE    = "";           // cambia si tu MySQL tiene clave
    private static final String SERVIDOR = "localhost:3306";
    private static final String BD       = "agenda_contactos";

    /**
     * Crea y devuelve una conexión nueva a la base de datos.
     * Cada invocación retorna una conexión independiente que debe cerrarse
     * con try-with-resources al terminar de usarla.
     *
     * @return Connection objeto de conexión
     */
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + SERVIDOR + "/" + BD
                       + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            con = DriverManager.getConnection(url, USUARIO, CLAVE);
        } catch (ClassNotFoundException ex) {
            System.err.println("[DatabaseConnection] Driver no encontrado: " + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println("[DatabaseConnection] Error al conectar con la BD: " + ex.getMessage());
        }
        return con;
    }
}
