package TP2.EX2TCP;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String clientInfo = socket.getInetAddress() + ":" + socket.getPort();
        System.out.println("Début du traitement pour le client: " + clientInfo);

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            // Message de bienvenue
            out.println("=== BIENVENUE DANS LE SYSTÈME BANCAIRE ===");
            out.println("Services disponibles :");
            out.println("1 - Bonus (+25% du salaire brut)");
            out.println("2 - Insurance (-5% du salaire brut)");
            out.println("3 - Tax (-15% du salaire brut)");
            out.println("4 - Salaire Net (application de tous les traitements)");
            out.println("0 - Quitter");

            ServiceImpl serviceImpl = new ServiceImpl();
            boolean continuer = true;

            while (continuer) {
                // Demander explicitement le salaire
                out.println(">>> Veuillez saisir votre salaire brut (ou 0 pour quitter) : ");
                String saisieSalaire = in.readLine();

                // Vérifier si l'utilisateur veut quitter
                if (saisieSalaire == null || saisieSalaire.equals("0")) {
                    out.println("Au revoir !");
                    continuer = false;
                    continue;
                }

                try {
                    double salaire = Double.parseDouble(saisieSalaire);
                    if (salaire < 0) {
                        out.println("Erreur : Le salaire ne peut pas être négatif.");
                        continue;
                    }

                    // Demander explicitement le service
                    out.println(">>> Choisissez un service (1-4) ou 0 pour quitter : ");
                    String option = in.readLine();

                    if (option == null || option.equals("0")) {
                        out.println("Au revoir !");
                        continuer = false;
                        continue;
                    }

                    double resultat = 0;
                    String message = "";

                    switch (option) {
                        case "1":
                            resultat = serviceImpl.traiterBonus(salaire);
                            message = String.format("Salaire après Bonus: %.2f → %.2f (+25%%)", salaire, resultat);
                            break;
                        case "2":
                            resultat = serviceImpl.traiterInsurance(salaire);
                            message = String.format("Salaire après Insurance: %.2f → %.2f (-5%%)", salaire, resultat);
                            break;
                        case "3":
                            resultat = serviceImpl.traiterTax(salaire);
                            message = String.format("Salaire après Tax: %.2f → %.2f (-15%%)", salaire, resultat);
                            break;
                        case "4":
                            resultat = serviceImpl.traiterSalaireNet(salaire);
                            message = String.format("Salaire Net: %.2f → %.2f (Bonus+Insurance+Tax)", salaire, resultat);
                            break;
                        default:
                            out.println("Option invalide. Veuillez choisir entre 1 et 4.");
                            continue;
                    }

                    out.println("RÉSULTAT: " + message);

                    // Demander si le client veut continuer
                    out.println(">>> Voulez-vous effectuer une autre opération ? (oui/non)");
                    String reponse = in.readLine();
                    if (reponse != null && (reponse.equalsIgnoreCase("non") || reponse.equalsIgnoreCase("n"))) {
                        out.println("Merci d'avoir utilisé nos services. Au revoir !");
                        continuer = false;
                    }

                } catch (NumberFormatException e) {
                    out.println("Erreur : Veuillez entrer un nombre valide pour le salaire.");
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur avec le client " + clientInfo + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Client déconnecté: " + clientInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}