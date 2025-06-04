/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package loginapp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class LoginApp {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root"; 
        String password = ""; 

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS seguridad_db");
            System.out.println("Base de datos creada.");

            stmt.execute("USE seguridad_db");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL,
                    password VARCHAR(100) NOT NULL
                )
            """);
            System.out.println("Tabla 'users' creada.");

            stmt.executeUpdate("""
                INSERT INTO users (username, password)
                VALUES ('admin', 'admin123'), ('usuario', 'pass123')
            """);
            System.out.println("Registros insertados.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


