package TP2.EX2UDP;

import java.net.*;
import java.util.concurrent.*;

public class ServerCode {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(4); // Limite à 4 clients simultanés

        try (DatagramSocket serverSocket = new DatagramSocket(5000)) {
            System.out.println("Serveur UDP démarré sur le port 5000...");
            System.out.println("En attente de connexions clients (max 4 simultanés)...");

            byte[] receiveData = new byte[1024];

            while (true) {
                // Attendre un message d'un client
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Nouveau client connecté. Adresse: " + clientAddress);
                System.out.println("Clients actifs: " + ((ThreadPoolExecutor) pool).getActiveCount() + "/4");

                // Démarrer un thread pour gérer ce client
                pool.execute(new ClientHandler(serverSocket, clientAddress, clientPort));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}