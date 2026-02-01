package `in`.org.dawn.helm.comms

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object Lantern {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect(hostName: String, secure: Boolean) {
        val url = if (secure) "wss://$hostName" else "ws://$hostName"
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Connected to ESP!")
            }
        })
    }

    fun sendCommand(instruction: String, token: String) {
        webSocket?.send("$token~$instruction")
    }
}