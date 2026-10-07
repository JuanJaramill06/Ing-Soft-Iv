import apexstore.*;
import java.sql.*;
import com.zeroc.Ice.Current;

public class DBPostgreSQLTransacciones implements RepositorioTransacciones {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public DBPostgreSQLTransacciones(String url, String user, String password) {
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPassword = password;
        crearTabla();
    }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS transacciones (" +
            "id_transaccion VARCHAR(36) PRIMARY KEY," +
            "monto DECIMAL(19, 2) NOT NULL," +
            "moneda VARCHAR(3) NOT NULL," +
            "medio_pago VARCHAR(20) NOT NULL," +
            "estado VARCHAR(20) NOT NULL," +
            "id_externo VARCHAR(100)," +
            "motivo VARCHAR(255)," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (java.sql.Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void persistirTransaccion(Transaccion transaccion, Current current) {
        String sql = "INSERT INTO transacciones " +
            "(id_transaccion, monto, moneda, medio_pago, estado, id_externo, motivo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (id_transaccion) DO UPDATE SET " +
            "estado = EXCLUDED.estado, " +
            "id_externo = EXCLUDED.id_externo, " +
            "motivo = EXCLUDED.motivo";

        try (java.sql.Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaccion.idTransaccion);
            pstmt.setDouble(2, transaccion.monto);
            pstmt.setString(3, transaccion.moneda);
            pstmt.setString(4, transaccion.medioPago);
            pstmt.setString(5, transaccion.estado.toString());
            pstmt.setString(6, transaccion.idExterno);
            pstmt.setString(7, transaccion.motivo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private java.sql.Connection getConnection() throws SQLException {
        return java.sql.DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
