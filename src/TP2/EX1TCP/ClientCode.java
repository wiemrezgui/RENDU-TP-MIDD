package TP2.EX1TCP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientCode {

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);

        // Afficher le message de bienvenue
        System.out.println(in.readLine());

        boolean continuer = true;

        while (continuer) {
            // Afficher le menu ligne par ligne
            System.out.println("\n" + "=".repeat(40));
            String ligne;
            while (!(ligne = in.readLine()).equals("END_MENU")) {
                System.out.println(ligne);
            }

            // Choix utilisateur
            int choix = getChoixValide(sc);
            out.println(choix);

            // Si choix = 0, on quitte
            if (choix == 0) {
                System.out.println(in.readLine()); // Message d'au revoir
                break;
            }

            // Traitement selon le choix
            traiterChoix(choix, in, out, sc);

            // Afficher le résultat
            System.out.println("\nRésultat : " + in.readLine());

            // Demander si on continue
            String signal = in.readLine();
            if (signal.equals("CONTINUE_QUESTION")) {
                continuer = demanderContinuer(sc, out);
            }
        }

        System.out.println("Déconnexion...");
        socket.close();
        sc.close();
    }

    private static int getChoixValide(Scanner sc) {
        while (true) {
            System.out.print("\nVotre choix (0-4) : ");
            if (sc.hasNextInt()) {
                int choix = sc.nextInt();
                if (choix >= 0 && choix <= 4) {
                    return choix;
                }
            } else {
                sc.next(); // Vider la mauvaise entrée
            }
            System.out.println("Erreur : Le choix doit être entre 0 et 4.");
        }
    }

    private static void traiterChoix(int choix, BufferedReader in, PrintWriter out, Scanner sc) throws IOException {
        switch (choix) {
            case 1 -> {
                System.out.print(in.readLine()); // "Entrer n : "
                out.println(getEntierValide(sc));
            }
            case 2 -> {
                System.out.print(in.readLine()); // "Base : "
                out.println(getEntierValide(sc));
                System.out.print(in.readLine()); // "Exposant : "
                out.println(getEntierValide(sc));
            }
            case 3 -> {
                System.out.print(in.readLine()); // "Entrer x : "
                out.println(getEntierValide(sc));
            }
            case 4 -> {
                System.out.print(in.readLine()); // "Entrer a : "
                out.println(getDoubleValide(sc));
                System.out.print(in.readLine()); // "Entrer b : "
                out.println(getDoubleValide(sc));
                System.out.print(in.readLine()); // "Entrer c : "
                out.println(getDoubleValide(sc));
            }
        }
    }

    private static boolean demanderContinuer(Scanner sc, PrintWriter out) {
        while (true) {
            System.out.print("\nVoulez-vous continuer ? (oui/non) : ");
            String reponse = sc.next().toLowerCase();

            if (reponse.equals("oui") || reponse.equals("o")) {
                out.println("oui");
                return true;
            } else if (reponse.equals("non") || reponse.equals("n")) {
                out.println("non");
                return false;
            } else {
                System.out.println("Veuillez répondre par 'oui' ou 'non'");
            }
        }
    }

    private static int getEntierValide(Scanner sc) {
        while (true) {
            if (sc.hasNextInt()) {
                return sc.nextInt();
            } else {
                System.out.print("Veuillez entrer un nombre entier valide : ");
                sc.next(); // Vider la mauvaise entrée
            }
        }
    }

    private static double getDoubleValide(Scanner sc) {
        while (true) {
            if (sc.hasNextDouble()) {
                return sc.nextDouble();
            } else {
                System.out.print("Veuillez entrer un nombre décimal valide : ");
                sc.next(); // Vider la mauvaise entrée
            }
        }
    }
}