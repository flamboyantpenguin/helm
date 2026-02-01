package `in`.org.dawn.helm.comms

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class Lantern(private val token: String) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect(ipAddress: String) {
        val request = Request.Builder().url("ws://$ipAddress/ws").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Connected to ESP!")
            }
        })
    }

    fun sendCommand(instruction: String) {
        webSocket?.send("$token:$instruction")
    }
}