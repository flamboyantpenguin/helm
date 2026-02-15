package `in`.org.dawn.helm.comms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object Lantern {
    var ready by mutableStateOf(false)
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect(hostName: String, secure: Boolean) {
        val url = if (secure) "wss://$hostName" else "ws://$hostName"
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                ready = true
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                ready = false
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                ready = false
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                ready = false
            }
        })
    }

    fun disconnect() {
        ready = false

        webSocket?.close(1000, "Lights off")

        webSocket = null
    }

    fun sendActuation(x: Float, y: Float, token: String) {
        webSocket?.send("$token~{$x:$y}")
    }

    fun sendCtrl(instruction: String, token: String) {
        webSocket?.send("$token~[$instruction]")
    }

}

fun igniteLantern(host: String, secure: Boolean) {
    if (Lantern.ready) Lantern.disconnect()
    Lantern.connect(host, secure)
}