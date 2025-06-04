/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginapp;
import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.time.Duration;

public class LoginSeguro {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/seguridad_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final int MAX_INTENTOS = 3;
    private static final int BLOQUEO_MINUTOS = 5;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Usuario: ");
        String username = sc.nextLine();
        System.out.print("Contraseña: ");
        String password = sc.nextLine();

        String passwordHash = hashPassword(password);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int intentos = rs.getInt("failed_attempts");
                    Timestamp last = rs.getTimestamp("last_attempt");
                    String passwordDb = rs.getString("password");

                    // Verificar si está bloqueado
                    if (intentos >= MAX_INTENTOS && last != null) {
                        long minutosDesdeUltimoIntento = Duration.between(
                            last.toInstant(), new java.util.Date().toInstant()).toMinutes();

                        if (minutosDesdeUltimoIntento < BLOQUEO_MINUTOS) {
                            System.out.println("Cuenta bloqueada. Intenta en " +
                                (BLOQUEO_MINUTOS - minutosDesdeUltimoIntento) + " minutos.");
                            return;
                        }
                    }

                    // Verificar contraseña
                    if (passwordDb.equals(passwordHash)) {
                        resetFailedAttempts(conn, username);
                        System.out.println("Inicio de sesión correcto.");
                    } else {
                        aumentarIntentoFallido(conn, username);
                        System.out.println("Usuario o contraseña incorrectos.");
                    }

                } else {
                    System.out.println("Usuario o contraseña incorrectos.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }

    // Hashear contraseña con SHA-256
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    // Aumentar contador de intentos fallidos
    private static void aumentarIntentoFallido(Connection conn, String username) throws SQLException {
        String sql = """
            UPDATE users
            SET failed_attempts = failed_attempts + 1, last_attempt = CURRENT_TIMESTAMP
            WHERE username = ?
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    // Reiniciar intentos fallidos después de login correcto
    private static void resetFailedAttempts(Connection conn, String username) throws SQLException {
        String sql = """
            UPDATE users
            SET failed_attempts = 0, last_attempt = NULL
            WHERE username = ?
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
}
