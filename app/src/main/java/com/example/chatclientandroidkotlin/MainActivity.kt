package com.example.chatclientandroidkotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private val connection: Connection? = connectToDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        userList = ArrayList()
        adapter =
            UserAdapter(this, userList, object : UserAdapter.OnUserClickListener {
                override fun onUserClick(user: User) {
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    intent.putExtra("name", user.name)
                    startActivity(intent)
                }
            })


        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        fetchUserData()


        val userAdapter = UserAdapter(this, userList, object : UserAdapter.OnUserClickListener {
            override fun onUserClick(user: User) {
                val intent = Intent(this@MainActivity, ChatActivity::class.java)
                intent.putExtra("name", user.name)
                startActivity(intent)
            }
        })

        userRecyclerView.adapter = userAdapter

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
            Toast.makeText(
                this@MainActivity,
                "Error connecting to database: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            null
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            val intent = Intent(this@MainActivity, LogIn::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }

    private fun fetchUserData() {
        connection?.use { conn ->
            try {
                val selectQuery = "SELECT * FROM users"
                val preparedStatement: PreparedStatement = conn.prepareStatement(selectQuery)
                val resultSet: ResultSet = preparedStatement.executeQuery()

                while (resultSet.next()) {
                    val name = resultSet.getString("name")
                    val email = resultSet.getString("email")
                    val password = resultSet.getString("password")
                    val user = User(name, email, password)
                    userList.add(user)
                }

                adapter.notifyDataSetChanged()

                preparedStatement.close()
            } catch (e: Exception) {
                println(e.message)
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "Error fetching user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun authenticateUser(email: String, pass: String): Boolean {

        connection?.use { conn ->
            try {
                val selectQuery = "SELECT * FROM users WHERE email = ? AND password = ?"
                val preparedStatement: PreparedStatement = conn.prepareStatement(selectQuery)
                preparedStatement.setString(1, email)
                preparedStatement.setString(2, pass)

                val resultSet: ResultSet = preparedStatement.executeQuery()

                return resultSet.next()

            } catch (e: Exception) {
                println(e.message)
                e.printStackTrace()
            }
        }
        return false
    }
}
