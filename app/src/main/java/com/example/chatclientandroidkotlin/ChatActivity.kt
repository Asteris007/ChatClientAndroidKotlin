package com.example.chatclientandroidkotlin

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket


class ChatActivity : AppCompatActivity(), MessageListener {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var socket: Socket
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private  val bufferedReader: BufferedReader
    private lateinit var message:Message



    private  val bufferedWriter: BufferedWriter

 

    init {
        bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    }
   // var receiveRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)





        val name = intent.getStringExtra("name")
        val passRec = intent.getStringExtra("pass")

        senderRoom = passRec + intent

        supportActionBar?.title = name



        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        messageBox = findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.send_img)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
      //  chatRecyclerView.adapter = messageAdapter

//to do...
            messageList.add(message)
messageList.clear()

        sendButton.setOnClickListener {

            val message = messageBox.text.toString()
            val messageObject = Message(message, senderRoom!!)
            messageList.add(messageObject)

            messageAdapter.notifyDataSetChanged()
        }


        chatRecyclerView.adapter = messageAdapter
    }



    override fun onMessageReceived(message: String) {

    }

}


