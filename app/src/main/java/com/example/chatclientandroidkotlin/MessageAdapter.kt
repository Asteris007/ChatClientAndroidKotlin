package com.example.chatclientandroidkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet


class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val connection: Connection? = connectToDatabase()


    val ITEM_RECEIVE=1;
    val ITEM_SEND=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_SEND -> {
                val view:View = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
                SentViewHolder(view)
            }
            ITEM_RECEIVE -> {
                val view:View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
                ReceiveViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder is SentViewHolder) {
            holder.sendMessage.text = currentMessage.message
        } else if (holder is ReceiveViewHolder) {
            holder.receiveMessage.text = currentMessage.message
        }
    }

    fun getItemViewType(position: Int, pass:User):Int{
        val currentMessage = messageList[position]
        val authenticatedUserId = 1
        return if (authenticateUser(authenticatedUserId,pass).equals(pass)) {
            ITEM_SEND
        } else {
            ITEM_RECEIVE
        }
    }
    private fun authenticateUser(authenticatedUserId: Int, pass: User): Boolean {
        connection?.use { conn ->
            try {
                val selectQuery = "SELECT * FROM users WHERE  password = ?"
                val preparedStatement: PreparedStatement = conn.prepareStatement(selectQuery)
                preparedStatement.setString(1, pass.toString())
                val resultSet: ResultSet = preparedStatement.executeQuery()
                return resultSet.next() && resultSet.getInt("user_id") == authenticatedUserId
            } catch (e: Exception) {
                println(e.message)
                e.printStackTrace()
            }
        }
        return false
    }

    private fun connectToDatabase(): Connection? {
        val url = "jdbc:postgresql://127.0.0.1:5432/ChatDB/User"
        val userP = "postgres"
        val passPsql = "123456"

        return try {
            DriverManager.getConnection(url, userP, passPsql)
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            null
        }
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.txt_send_message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
    }

}
