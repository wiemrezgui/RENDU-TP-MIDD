package TP2.EX1UDP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientCode {

    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 5000;
        Scanner sc = new Scanner(System.in);

        byte[] sendData;
        byte[] receiveData = new byte[1024];

        // Envoyer un premier message au serveur
        sendMessage(clientSocket, serverAddress, serverPort, "CONNECT");

        // Recevoir le message de bienvenue
        String welcomeMessage = receiveMessage(clientSocket, receiveData);
        System.out.println(welcomeMessage);

        boolean continuer = true;

        while (continuer) {
            // Recevoir et afficher le menu
            String menu = receiveMessage(clientSocket, receiveData);
            System.out.println("\n" + "=".repeat(40));
            System.out.println(menu);

            // Choix utilisateur
            int choix = getChoixValide(sc);
            sendMessage(clientSocket, serverAddress, serverPort, String.valueOf(choix));

            // Si choix = 0, on quitte
            if (choix == 0) {
                String goodbyeMessage = receiveMessage(clientSocket, receiveData);
                System.out.println(goodbyeMessage);
                break;
            }

            // Traitement selon le choix
            traiterChoixUDP(choix, clientSocket, serverAddress, serverPort, receiveData, sc);

            // Afficher le résultat
            String result = receiveMessage(clientSocket, receiveData);
            System.out.println("\nRésultat : " + result);

            // Demander si on continue
            String signal = receiveMessage(clientSocket, receiveData);
            if (signal.equals("CONTINUE_QUESTION")) {
                continuer = demanderContinuerUDP(sc, clientSocket, serverAddress, serverPort);
            }
        }

        System.out.println("Déconnexion...");
        clientSocket.close();
        sc.close();
    }

    private static void traiterChoixUDP(int choix, DatagramSocket socket, InetAddress address, int port,
                                        byte[] receiveData, Scanner sc) throws IOException {
        switch (choix) {
            case 1 -> {
                String prompt = receiveMessage(socket, receiveData);
                System.out.print(prompt);
                sendMessage(socket, address, port, String.valueOf(getEntierValide(sc)));
            }
            case 2 -> {
                String prompt1 = receiveMessage(socket, receiveData);
                System.out.print(prompt1);
                sendMessage(socket, address, port, String.valueOf(getEntierValide(sc)));

                String prompt2 = receiveMessage(socket, receiveData);
                System.out.print(prompt2);
                sendMessage(socket, address, port, String.valueOf(getEntierValide(sc)));
            }
            case 3 -> {
                String prompt = receiveMessage(socket, receiveData);
                System.out.print(prompt);
                sendMessage(socket, address, port, String.valueOf(getEntierValide(sc)));
            }
            case 4 -> {
                String prompt1 = receiveMessage(socket, receiveData);
                System.out.print(prompt1);
                sendMessage(socket, address, port, String.valueOf(getDoubleValide(sc)));

                String prompt2 = receiveMessage(socket, receiveData);
                System.out.print(prompt2);
                sendMessage(socket, address, port, String.valueOf(getDoubleValide(sc)));

                String prompt3 = receiveMessage(socket, receiveData);
                System.out.print(prompt3);
                sendMessage(socket, address, port, String.valueOf(getDoubleValide(sc)));
            }
        }
    }

    private static boolean demanderContinuerUDP(Scanner sc, DatagramSocket socket,
                                                InetAddress address, int port) throws IOException {
        while (true) {
            System.out.print("\nVoulez-vous continuer ? (oui/non) : ");
            String reponse = sc.next().toLowerCase();

            if (reponse.equals("oui") || reponse.equals("o")) {
                sendMessage(socket, address, port, "oui");
                return true;
            } else if (reponse.equals("non") || reponse.equals("n")) {
                sendMessage(socket, address, port, "non");
                return false;
            } else {
                System.out.println("Veuillez répondre par 'oui' ou 'non'");
            }
        }
    }

    // Méthode pour envoyer un message UDP
    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        socket.send(sendPacket);
    }

    // Méthode pour recevoir un message UDP
    private static String receiveMessage(DatagramSocket socket, byte[] receiveData) throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
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