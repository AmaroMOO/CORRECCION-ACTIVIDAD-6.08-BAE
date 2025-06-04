/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginapp;
import java.sql.*;
import java.util.Scanner;

public class Validacion {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Usuario: ");
        String username = sc.nextLine();
        System.out.print("Contraseña: ");
        String password = sc.nextLine();

        String url = "jdbc:mysql://localhost:3306/seguridad_db";
        String dbUser = "root"; 
        String dbPassword = ""; 

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        System.out.println("Consulta SQL preparada: " + query);

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Inicio de sesión correcto.");
            } else {
                System.out.println("Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }
}

