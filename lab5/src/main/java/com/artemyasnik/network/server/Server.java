package com.artemyasnik.network.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    public static void main(String[] args) {
        try {
            byte[] buffer = new byte[10];  // Буфер для приема данных
            int port = 6789;  // Порт сервера

            // Создаем UDP-сокет на указанном порту
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println("Сервер запущен и ожидает данные...");

            // Пакет для приема данных
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);  // Ожидаем данные от клиента

            // Умножаем каждый байт на 2
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] *= 2;
            }

            // Получаем адрес и порт клиента
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            // Отправляем измененные данные обратно клиенту
            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(sendPacket);
            System.out.println("Данные отправлены клиенту.");

            socket.close();  // Закрываем сокет
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}