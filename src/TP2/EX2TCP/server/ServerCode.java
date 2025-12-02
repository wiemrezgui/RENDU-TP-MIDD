package TP2.EX2TCP.server;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerCode {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(4); // Limite à 4 clients simultanés

        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("Serveur démarré sur le port 5000...");
            System.out.println("En attente de connexions clients (max 4 simultanés)...");

            while (true) {
                Socket client = server.accept();
                System.out.println("Nouveau client connecté. Adresse: " + client.getInetAddress());
                System.out.println("Clients actifs: " + ((ThreadPoolExecutor) pool).getActiveCount() + "/4");

                pool.execute(new ClientHandler(client));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}