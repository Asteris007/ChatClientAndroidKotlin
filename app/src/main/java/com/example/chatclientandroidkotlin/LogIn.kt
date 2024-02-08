package com.example.chatclientandroidkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class LogIn : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var buttLogin: Button
    private lateinit var buttSignIn: Button

    private val connection: Connection? = connectToDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.edit_email)
        edtPassword = findViewById(R.id.edit_pass)
        buttLogin = findViewById(R.id.button_log_in)
        buttSignIn = findViewById(R.id.button_sign_in)


        buttSignIn.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            finish()
            startActivity(intent)
        }

        buttLogin.setOnClickListener{
            val email =edtEmail.text.toString()
            val password = edtPassword.text.toString()

            login(email, password);
        }

    }
    private fun connectToDatabase(): Connection? {
        val url = "jdbc:postgresql://127.0.0.1:5432/User"
        val userP = "postgres"
        val passPsql = "123456"

        return try {
            DriverManager.getConnection(url, userP, passPsql)
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            Toast.makeText(this@LogIn, "Error connecting to database: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }
    private fun login(email: String, password: String){
        val url= "jdbc:postgresql://127.0.0.1:5432/User";
        val userP = "postgres"
        val passPsql = "123456"

        try {
            val connection: Connection = DriverManager.getConnection(url, userP, passPsql)
            Toast.makeText(this@LogIn, "Connection successful", Toast.LENGTH_SHORT).show()

            connection.close()

        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }
        authenticateUser(email,password)
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