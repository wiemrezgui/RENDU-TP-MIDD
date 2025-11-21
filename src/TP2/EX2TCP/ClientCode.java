package TP2.EX2TCP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientCode {

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner sc = new Scanner(System.in);
        ) {
            System.out.println("Connexion au serveur établie...");

            String serverMsg;

            while ((serverMsg = in.readLine()) != null) {

                // Afficher tout ce que le serveur envoie
                System.out.println(serverMsg);

                // NE répondre que si une ligne contient ">>>"
                if (serverMsg.trim().startsWith(">>>")) {
                    String userInput = sc.nextLine();
                    out.println(userInput);

                    if (userInput.equals("0")) {
                        break;
                    }
                }

                // Condition de sortie
                if (serverMsg.contains("Au revoir") || serverMsg.contains("Merci")) {
                    break;
                }
            }

            System.out.println("Déconnexion du serveur.");

        } catch (Exception e) {
            System.out.println("Erreur client : " + e.getMessage());
        }
    }
}
