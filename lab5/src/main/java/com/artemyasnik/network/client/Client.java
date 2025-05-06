package com.artemyasnik.network.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) {
        try {
            byte[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};  // Данные для отправки
            String serverAddress = "localhost";  // Адрес сервера (можно заменить на IP)
            int serverPort = 6789;  // Порт сервера

            // Создаем UDP-сокет (без указания порта, система выберет случайный)
            DatagramSocket socket = new DatagramSocket();
            System.out.println("Клиент запущен. Отправка данных...");

            // Отправляем данные серверу
            InetAddress address = InetAddress.getByName(serverAddress);
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, serverPort);
            socket.send(sendPacket);

            // Получаем ответ от сервера
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);
            socket.receive(receivePacket);
            System.out.println("Получены данные от сервера:");

            // Выводим полученные данные
            for (byte b : data) {
                System.out.print(b + " ");
            }

            socket.close();  // Закрываем сокет
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
