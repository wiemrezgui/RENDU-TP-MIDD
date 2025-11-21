package BINGO;

// ClientBingo.java
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientBingo {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public ClientBingo() {
        scanner = new Scanner(System.in);
    }

    public void connecter() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   Connecté au serveur Bingo avec succès!   ║");
            System.out.println("╚════════════════════════════════════════╝");

        } catch (IOException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            System.exit(1);
        }
    }

    public void afficherMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          MENU PRINCIPAL BINGO          ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. Jouer au BINGO                     ║");
        System.out.println("║  2. Quitter                            ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Votre choix: ");
    }

    public void jouerBingo() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       NOUVELLE PARTIE DE BINGO         ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Vous avez 10 tentatives pour prédire les boules (0-9)");

        int scoreTotal = 0;

        for (int tentative = 1; tentative <= 10; tentative++) {
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("         TENTATIVE " + tentative + "/10");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            int[] prediction = saisirPrediction();

            // Envoyer la prédiction au serveur via le Gateway
            String requete = "JOUER:" + arrayToString(prediction);
            out.println(requete);

            try {
                String response = in.readLine();

                if (response.startsWith("RESULTAT:")) {
                    String[] parts = response.substring(9).split(",");
                    int score = Integer.parseInt(parts[0]);
                    scoreTotal += score;

                    int[] boulesTirees = new int[10];
                    for (int i = 0; i < 10; i++) {
                        boulesTirees[i] = Integer.parseInt(parts[i + 1]);
                    }

                    afficherResultat(prediction, boulesTirees, score, scoreTotal);
                } else if (response.startsWith("ERREUR:")) {
                    System.out.println("Erreur "+ response.substring(7));
                }

            } catch (IOException e) {
                System.err.println("Erreur de communication: " + e.getMessage());
                return;
            }
        }

        // Afficher le score final
        afficherScoreFinal(scoreTotal);
    }

    private int[] saisirPrediction() {
        int[] prediction = new int[10];
        Set<Integer> dejaSaisi = new HashSet<>();

        System.out.println("Prédisez 10 boules (numéros de 0 à 9, tous différents):");

        int count = 0;
        while (count < 10) {
            System.out.print("Boule " + (count + 1) + ": ");
            try {
                int num = scanner.nextInt();

                if (num < 0 || num > 9) {
                    System.out.println("  Le numéro doit être entre 0 et 9!");
                    continue;
                }

                if (dejaSaisi.contains(num)) {
                    System.out.println("  Vous avez déjà saisi ce numéro!");
                    continue;
                }

                prediction[count] = num;
                dejaSaisi.add(num);
                count++;

            } catch (InputMismatchException e) {
                System.out.println("  Veuillez entrer un nombre valide!");
                scanner.next(); // Vider le buffer
            }
        }

        return prediction;
    }

    private void afficherResultat(int[] prediction, int[] boulesTirees, int score, int scoreTotal) {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│         RÉSULTAT DU TIRAGE          │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.print("│ Votre prédiction : ");
        System.out.println(Arrays.toString(prediction) + " │");
        System.out.print("│ Boules tirées    : ");
        System.out.println(Arrays.toString(boulesTirees) + " │");
        System.out.println("├─────────────────────────────────────┤");

        if (score == 10) {
            System.out.println("│ 🎉 PARFAIT! Score : " + score + "/10 🎉       │");
        } else if (score >= 7) {
            System.out.println("│ ✨ Excellent! Score : " + score + "/10          │");
        } else if (score >= 5) {
            System.out.println("│ 👍 Bien! Score : " + score + "/10              │");
        } else {
            System.out.println("│ Score : " + score + "/10                      │");
        }

        System.out.println("│ Score total : " + scoreTotal + " points            │");
        System.out.println("└─────────────────────────────────────┘");
    }

    private void afficherScoreFinal(int scoreTotal) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          PARTIE TERMINÉE!              ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  Score final: " + scoreTotal + "/100                  ║");

        double pourcentage = (scoreTotal * 100.0) / 100.0;
        System.out.println("║  Taux de réussite: " + String.format("%.1f", pourcentage) + "%            ║");

        if (scoreTotal >= 80) {
            System.out.println("║  🏆 Performance exceptionnelle! 🏆     ║");
        } else if (scoreTotal >= 60) {
            System.out.println("║  ⭐ Très bonne performance! ⭐         ║");
        } else if (scoreTotal >= 40) {
            System.out.println("║  👏 Bonne performance!                 ║");
        } else {
            System.out.println("║  💪 Continue à t'entraîner!            ║");
        }

        System.out.println("╚════════════════════════════════════════╝");
    }

    public void quitter() {
        try {
            out.println("QUITTER:");
            String response = in.readLine();
            System.out.println("\n" + response);

            socket.close();
            scanner.close();

            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║    Merci d'avoir joué au BINGO!        ║");
            System.out.println("║           À bientôt!                 ║");
            System.out.println("╚════════════════════════════════════════╝");

        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion");
        }
    }

    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public void demarrer() {
        connecter();

        boolean continuer = true;
        while (continuer) {
            afficherMenu();

            try {
                int choix = scanner.nextInt();
                scanner.nextLine(); // Consommer le retour à la ligne

                switch (choix) {
                    case 1:
                        jouerBingo();
                        break;
                    case 2:
                        quitter();
                        continuer = false;
                        break;
                    default:
                        System.out.println("  Choix invalide. Veuillez choisir 1 ou 2 .");
                }
            } catch (InputMismatchException e) {
                System.out.println("  Veuillez entrer un nombre valide!");
                scanner.next(); // Vider le buffer
            }
        }
    }

    public static void main(String[] args) {
        ClientBingo client = new ClientBingo();
        client.demarrer();
    }
}
