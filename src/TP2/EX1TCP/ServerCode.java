package TP2.EX1TCP;

import java.io.*;
import java.net.*;

public class ServerCode {

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(5000);
        System.out.println("Serveur prêt, attente d'un client...");

        Socket socket = server.accept();
        System.out.println("Client connecté.");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        MathOpsImpl ops = new MathOpsImpl();

        // Message de bienvenue
        out.println("Connexion réussie. Je vous écoute clairement.");

        boolean continuer = true;

        while (continuer) {
            // Envoi du menu
            out.println("=== MENU ===");
            out.println("1 - Factorielle");
            out.println("2 - Puissance");
            out.println("3 - Racine carrée");
            out.println("4 - Équation du second degré");
            out.println("0 - Quitter");
            out.println("END_MENU");

            // Lecture du choix
            int choix = Integer.parseInt(in.readLine());

            // Si choix = 0, on quitte
            if (choix == 0) {
                out.println("Au revoir !");
                continuer = false;
                continue;
            }

            String result = "";

            switch (choix) {
                case 1 -> {
                    out.println("Entrer n : ");
                    int n = Integer.parseInt(in.readLine());
                    result = "Factorielle = " + ops.factorielle(n);
                }
                case 2 -> {
                    out.println("Base : ");
                    int base = Integer.parseInt(in.readLine());
                    out.println("Exposant : ");
                    int exp = Integer.parseInt(in.readLine());
                    result = "Puissance = " + ops.puissance(base, exp);
                }
                case 3 -> {
                    out.println("Entrer x : ");
                    int x = Integer.parseInt(in.readLine());
                    result = "Racine carrée = " + ops.racineCarree(x);
                }
                case 4 -> {
                    out.println("Entrer a : ");
                    double a = Double.parseDouble(in.readLine());
                    out.println("Entrer b : ");
                    double b = Double.parseDouble(in.readLine());
                    out.println("Entrer c : ");
                    double c = Double.parseDouble(in.readLine());
                    result = ops.equationSecondDegre(a, b, c);
                }
                default -> result = "Choix invalide.";
            }

            // Envoyer le résultat
            out.println(result);

            // Attendre la confirmation pour continuer
            out.println("CONTINUE_QUESTION");
            String reponse = in.readLine();
            if (reponse.equalsIgnoreCase("non") || reponse.equalsIgnoreCase("n")) {
                continuer = false;
                out.println("Merci d'avoir utilisé le service. Au revoir !");
            }
        }

        socket.close();
        server.close();
    }
}