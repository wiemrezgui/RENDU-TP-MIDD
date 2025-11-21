package BINGO;

// ClientBingoGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientBingoGUI extends JFrame {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    // Composants réseau
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Composants GUI
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private JButton[] bouleButtons;
    private Set<Integer> boulesSelectionnees;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JTextArea resultatArea;
    private JProgressBar progressBar;

    // Variables de jeu
    private int tentativeActuelle;
    private int scoreTotal;
    private int[] predictionActuelle;

    // Couleurs personnalisées
    private final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private final Color COLOR_SUCCESS = new Color(39, 174, 96);
    private final Color COLOR_WARNING = new Color(243, 156, 18);
    private final Color COLOR_DANGER = new Color(231, 76, 60);
    private final Color COLOR_SELECTED = new Color(52, 152, 219);
    private final Color COLOR_BACKGROUND = new Color(236, 240, 241);

    public ClientBingoGUI() {
        setTitle("🎲 Jeu BINGO - Client");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        boulesSelectionnees = new HashSet<>();
        tentativeActuelle = 0;
        scoreTotal = 0;

        initComponents();
        connecterAuServeur();
        afficherMenu();

        setVisible(true);
    }

    private void initComponents() {
        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(COLOR_BACKGROUND);

        // Panel Menu
        menuPanel = creerMenuPanel();

        // Panel Jeu
        gamePanel = creerGamePanel();

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);
    }

    private JPanel creerMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);

        // Titre
        JLabel titreLabel = new JLabel("BINGO GAME", SwingConstants.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titreLabel.setForeground(COLOR_PRIMARY);
        titreLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));

        // Panel des boutons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);

        // Bouton Jouer
        JButton jouerBtn = creerBoutonMenu("JOUER AU BINGO", COLOR_SUCCESS);
        jouerBtn.addActionListener(e -> demarrerPartie());
        gbc.gridy = 0;
        buttonsPanel.add(jouerBtn, gbc);

        // Bouton Meilleur Score
        JButton scoreBtn = creerBoutonMenu("MEILLEUR SCORE", COLOR_WARNING);
        scoreBtn.addActionListener(e -> afficherMeilleurScore());
        gbc.gridy = 1;
        buttonsPanel.add(scoreBtn, gbc);

        // Bouton Quitter
        JButton quitterBtn = creerBoutonMenu("QUITTER", COLOR_DANGER);
        quitterBtn.addActionListener(e -> quitter());
        gbc.gridy = 2;
        buttonsPanel.add(quitterBtn, gbc);

        // Label de statut
        statusLabel = new JLabel("Connecté au serveur", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(COLOR_SUCCESS);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        panel.add(titreLabel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel creerGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel du haut : Info tentative et score
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBackground(COLOR_BACKGROUND);

        scoreLabel = new JLabel("Score Total: 0/10", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(COLOR_PRIMARY);

        progressBar = new JProgressBar(0, 10);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.BOLD, 14));
        progressBar.setForeground(COLOR_SUCCESS);
        progressBar.setString("Tentative 0/10");

        topPanel.add(scoreLabel);
        topPanel.add(progressBar);

        // Panel central : Sélection des boules
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_BACKGROUND);

        JLabel instructionLabel = new JLabel("Sélectionnez 10 boules différentes (0-9):", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        instructionLabel.setForeground(COLOR_PRIMARY);

        JPanel boulesPanel = creerBoulesPanel();

        centerPanel.add(instructionLabel, BorderLayout.NORTH);
        centerPanel.add(boulesPanel, BorderLayout.CENTER);

        // Panel du bas : Résultats et boutons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(COLOR_BACKGROUND);

        resultatArea = new JTextArea(8, 40);
        resultatArea.setEditable(false);
        resultatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultatArea.setBackground(new Color(44, 62, 80));
        resultatArea.setForeground(Color.WHITE);
        resultatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultatArea);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(COLOR_BACKGROUND);

        JButton validerBtn = new JButton("VALIDER LA PRÉDICTION");
        validerBtn.setFont(new Font("Arial", Font.BOLD, 13));
        validerBtn.setBackground(COLOR_SUCCESS);
        validerBtn.setForeground(new Color(41, 128, 185));
        validerBtn.setFocusPainted(false);
        validerBtn.setPreferredSize(new Dimension(250, 50));
        validerBtn.addActionListener(e -> validerPrediction());

        JButton resetBtn = new JButton("RÉINITIALISER");
        resetBtn.setFont(new Font("Arial", Font.BOLD, 13));
        resetBtn.setBackground(COLOR_WARNING);
        resetBtn.setForeground(new Color(41, 128, 185));
        resetBtn.setFocusPainted(false);
        resetBtn.setPreferredSize(new Dimension(200, 50));
        resetBtn.addActionListener(e -> reinitialiserSelection());

        JButton menuBtn = new JButton("◄ MENU");
        menuBtn.setFont(new Font("Arial", Font.BOLD, 13));
        menuBtn.setBackground(COLOR_DANGER);
        menuBtn.setForeground(new Color(41, 128, 185));
        menuBtn.setFocusPainted(false);
        menuBtn.setPreferredSize(new Dimension(150, 50));
        menuBtn.addActionListener(e -> retourMenu());

        actionPanel.add(validerBtn);
        actionPanel.add(resetBtn);
        actionPanel.add(menuBtn);

        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel creerBoulesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 5, 15, 15));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        bouleButtons = new JButton[10];

        for (int i = 0; i < 10; i++) {
            final int numero = i;
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("Arial", Font.BOLD, 36));
            btn.setBackground(Color.WHITE);
            btn.setForeground(COLOR_PRIMARY);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 3));
            btn.setPreferredSize(new Dimension(80, 80));

            btn.addActionListener(e -> toggleBoule(numero, btn));

            bouleButtons[i] = btn;
            panel.add(btn);
        }

        return panel;
    }

    private JButton creerBoutonMenu(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Arial", Font.PLAIN, 20));
        btn.setBackground(couleur);
        btn.setForeground(new Color(41, 128, 185));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(400, 70));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(couleur.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(couleur);
            }
        });

        return btn;
    }

    private void toggleBoule(int numero, JButton btn) {
        if (boulesSelectionnees.contains(numero)) {
            // Désélectionner
            boulesSelectionnees.remove(numero);
            btn.setBackground(Color.WHITE);
            btn.setForeground(COLOR_PRIMARY);
        } else {
            // Sélectionner si moins de 10 boules
            if (boulesSelectionnees.size() < 10) {
                boulesSelectionnees.add(numero);
                btn.setBackground(COLOR_SELECTED);
                btn.setForeground(Color.WHITE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vous avez déjà sélectionné 10 boules!",
                        "Attention",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void reinitialiserSelection() {
        boulesSelectionnees.clear();
        for (JButton btn : bouleButtons) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(41, 128, 185));
        }
    }

    private void validerPrediction() {
        if (boulesSelectionnees.size() != 10) {
            JOptionPane.showMessageDialog(this,
                    "Vous devez sélectionner exactement 10 boules!",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convertir en tableau
        predictionActuelle = new int[10];
        int index = 0;
        for (int num : boulesSelectionnees) {
            predictionActuelle[index++] = num;
        }

        // Envoyer au serveur
        envoyerPrediction(predictionActuelle);

        // Réinitialiser pour la prochaine tentative
        reinitialiserSelection();
    }

    private void envoyerPrediction(int[] prediction) {
        try {
            StringBuilder sb = new StringBuilder("JOUER:");
            for (int i = 0; i < prediction.length; i++) {
                sb.append(prediction[i]);
                if (i < prediction.length - 1) sb.append(",");
            }

            out.println(sb.toString());
            String response = in.readLine();

            if (response.startsWith("RESULTAT:")) {
                traiterResultat(response, prediction);
            } else if (response.startsWith("ERREUR:")) {
                JOptionPane.showMessageDialog(this,
                        response.substring(7),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de communication avec le serveur",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void traiterResultat(String response, int[] prediction) {
        String[] parts = response.substring(9).split(",");
        int score = Integer.parseInt(parts[0]);
        scoreTotal += score;
        tentativeActuelle++;

        int[] boulesTirees = new int[10];
        for (int i = 0; i < 10; i++) {
            boulesTirees[i] = Integer.parseInt(parts[i + 1]);
        }

        // Mettre à jour l'affichage
        progressBar.setValue(tentativeActuelle);
        progressBar.setString("Tentative " + tentativeActuelle + "/10");
        scoreLabel.setText("Score Total: " + scoreTotal + "/100");

        // Afficher le résultat
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("       TENTATIVE ").append(tentativeActuelle).append("/10\n");
        sb.append("═══════════════════════════════════════════\n");
        sb.append("Votre prédiction : ").append(Arrays.toString(prediction)).append("\n");
        sb.append("Boules tirées    : ").append(Arrays.toString(boulesTirees)).append("\n");
        sb.append("───────────────────────────────────────────\n");
        sb.append("Score : ").append(score).append("/10");

        if (score == 10) {
            sb.append(" 🎉 PARFAIT!");
        } else if (score >= 7) {
            sb.append(" ✨ Excellent!");
        } else if (score >= 5) {
            sb.append(" 👍 Bien!");
        }

        sb.append("\nScore total : ").append(scoreTotal).append("/100\n");
        sb.append("═══════════════════════════════════════════\n\n");

        resultatArea.append(sb.toString());
        resultatArea.setCaretPosition(resultatArea.getDocument().getLength());

        // Vérifier fin de partie
        if (tentativeActuelle >= 10) {
            afficherFinPartie();
        }
    }

    private void afficherFinPartie() {
        SwingUtilities.invokeLater(() -> {
            String message = String.format(
                            "╔═══════════════════════════════════╗\n" +
                            "║     PARTIE TERMINÉE!              ║\n" +
                            "╠═══════════════════════════════════╣\n" +
                            "║  Score final: %d/10               ║\n" +
                            "║  Taux de réussite: %.1f%%         ║\n",
                    scoreTotal, (scoreTotal * 100.0) / 100.0
            );

            if (scoreTotal >= 80) {
                message += "║  🏆 Performance exceptionnelle! 🏆║\n";
            } else if (scoreTotal >= 60) {
                message += "║  ⭐ Très bonne performance! ⭐   ║\n";
            } else if (scoreTotal >= 40) {
                message += "║  👏 Bonne performance!            ║\n";
            } else {
                message += "║  💪 Continue à t'entraîner!       ║\n";
            }

            message += "╚═══════════════════════════════════╝";

            JOptionPane.showMessageDialog(this,
                    message,
                    "Fin de partie",
                    JOptionPane.INFORMATION_MESSAGE);

            retourMenu();
        });
    }

    private void demarrerPartie() {
        tentativeActuelle = 0;
        scoreTotal = 0;
        boulesSelectionnees.clear();
        resultatArea.setText("");
        scoreLabel.setText("Score Total: 0/100");
        progressBar.setValue(0);
        progressBar.setString("Tentative 0/10");

        for (JButton btn : bouleButtons) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(COLOR_PRIMARY);
        }

        afficherPanel("GAME");
    }

    private void afficherMeilleurScore() {
        try {
            out.println("MEILLEUR_SCORE:");
            String response = in.readLine();

            if (response.startsWith("SCORE:")) {
                int score = Integer.parseInt(response.substring(6));
                JOptionPane.showMessageDialog(this,
                        "🏆 Meilleur Score: " + score + "/10",
                        "Record",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la récupération du score",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void retourMenu() {
        afficherPanel("MENU");
    }

    private void afficherMenu() {
        afficherPanel("MENU");
    }

    private void afficherPanel(String panelName) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, panelName);
    }

    private void connecterAuServeur() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            statusLabel.setText("Connecté au serveur");
            statusLabel.setForeground(COLOR_SUCCESS);
        } catch (IOException e) {
            statusLabel.setText("Erreur de connexion");
            statusLabel.setForeground(COLOR_DANGER);
            JOptionPane.showMessageDialog(this,
                    "Impossible de se connecter au serveur!\nAssurez-vous que le serveur et le Gateway sont démarrés.",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitter() {
        int reponse = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment quitter?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            try {
                if (out != null) {
                    out.println("QUITTER:");
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ClientBingoGUI();
        });
    }
}
