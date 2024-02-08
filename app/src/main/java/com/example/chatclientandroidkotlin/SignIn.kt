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

class SignIn :  AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var buttSignIn: Button

    private val connection: Connection? = connectToDatabase()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

        edtName = findViewById(R.id.edit_name)
        edtEmail = findViewById(R.id.edit_email)
        edtPassword = findViewById(R.id.edit_pass)
        buttSignIn = findViewById(R.id.button_sign_in)

        buttSignIn.setOnClickListener {

            val email = edtEmail.text.toString()
            val pass = edtPassword.text.toString()
            val name = edtName.text.toString()

         signIn(name, email, pass);
        }
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
            Toast.makeText(this@SignIn, "Error connecting to database: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun signIn(name: String, email: String, pass: String) {
        connection?.use { conn ->
            try {
                val insertQuery = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)"
                val preparedStatement: PreparedStatement = conn.prepareStatement(insertQuery)
                preparedStatement.setString(1, name)
                preparedStatement.setString(2, email)
                preparedStatement.setString(3, pass)

                val rowsInserted = preparedStatement.executeUpdate()

                if (rowsInserted > 0) {
                    Toast.makeText(this@SignIn, "User created successfully", Toast.LENGTH_SHORT).show()

                    // Simulate user authentication
                    val isUserAuthenticated = authenticateUser(email, pass)

                    if (isUserAuthenticated) {
                        val intent = Intent(this@SignIn, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@SignIn, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignIn, "Failed to create user", Toast.LENGTH_SHORT).show()
                }
                preparedStatement.close()
            } catch (e: Exception) {
                println(e.message)
                e.printStackTrace()
                Toast.makeText(this@SignIn, "Error creating user: ${e.message}", Toast.LENGTH_SHORT).show()
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
