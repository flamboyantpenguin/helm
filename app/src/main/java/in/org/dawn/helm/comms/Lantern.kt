package `in`.org.dawn.helm.comms

import android.content.Context
import `in`.org.dawn.helm.prefs.LanternRepository
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class Lantern(private val context: Context, private val token: String) {

    suspend fun getHostName(): String {
        return repository.settingsFlow.first().host
    }

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val repository = LanternRepository(context)


    suspend fun connect() {
        val hostName = getHostName()
        val request = Request.Builder().url("ws://$hostName/ws").build()
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