package com.example.myapplication

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object UdpClient {

    private const val PI_IP = "10.199.1.5"   // your Pi / server IP
    private const val PORT = 9999

    fun askAI(question: String): String {
        val socket = DatagramSocket()
        socket.soTimeout = 150_000   // Pi is slow

        val address = InetAddress.getByName(PI_IP)

        // send
        val sendData = question.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, address, PORT)
        socket.send(sendPacket)

        // receive
        val buffer = ByteArray(4096)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        socket.receive(receivePacket)

        socket.close()

        return String(receivePacket.data, 0, receivePacket.length)
    }
}
