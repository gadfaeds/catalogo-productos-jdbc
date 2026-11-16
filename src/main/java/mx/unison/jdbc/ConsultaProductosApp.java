package mx.unison.jdbc;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class ConsultaProductosApp {
    private static final String SQL = """
        SELECT
            id_producto,
            nombre,
            categoria,
            precio,
            existencia
        FROM producto
        WHERE precio >= ?
          AND categoria = ?
        ORDER BY precio
        """;
    public static void main(String[] args) {
        String url = obtenerVariable(
                "DB_URL",
                "jdbc:postgresql://localhost:5432/laboratorio_git_jdbc"
        );
        String usuario = System.getenv("DB_USER");
        String contrasena = System.getenv("DB_PASSWORD");
        if (usuario == null || contrasena == null) {
            System.err.println(
                    "Defina las variables DB_USER y DB_PASSWORD."
            );
            return;
        }
        BigDecimal precioMinimo = obtenerPrecio(args);
        String categoria = args.length > 1
                ? args[1]
                : "Periféricos";
        try (
                Connection conexion = DriverManager.getConnection(
                        url,
                        usuario,
                        contrasena
                );
                PreparedStatement sentencia =
                        conexion.prepareStatement(SQL)
        ) {
            sentencia.setBigDecimal(1, precioMinimo);
            sentencia.setString(2, categoria);
            try (ResultSet resultados = sentencia.executeQuery()) {
                mostrarResultados(resultados);
            }
        } catch (SQLException exception) {
            System.err.println(
                    "Error de base de datos: " + exception.getMessage()
            );
            System.err.println(
                    "Estado SQL: " + exception.getSQLState()
            );
        }
    }
    private static BigDecimal obtenerPrecio(String[] args) {
        if (args.length == 0) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(args[0]);
        } catch (NumberFormatException exception) {
            System.err.println(
                    "Precio incorrecto; se utilizará cero."
            );
            return BigDecimal.ZERO;
        }
    }
    private static void mostrarResultados(ResultSet resultados)
            throws SQLException {
        System.out.printf(
                "%-4s %-25s %-18s %10s %10s%n",
                "ID",
                "NOMBRE",
                "CATEGORÍA",
                "PRECIO",
                "EXISTENCIA"
        );
        while (resultados.next()) {
            System.out.printf(
                    "%-4d %-25s %-18s %10.2f %10d%n",
                    resultados.getInt("id_producto"),
                    resultados.getString("nombre"),
                    resultados.getString("categoria"),
                    resultados.getBigDecimal("precio"),
                    resultados.getInt("existencia")
            );
        }
    }
    private static String obtenerVariable(
            String nombre,
            String predeterminado
    ) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank()
                ? predeterminado
                : valor;
    }
}
