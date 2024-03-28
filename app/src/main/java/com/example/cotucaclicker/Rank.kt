package com.example.cotucaclicker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import java.sql.ResultSet
import java.sql.SQLException

class Rank : MainActivity() {

    private lateinit var edNome : EditText
    private lateinit var btnConfirmar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)
        btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        edNome = findViewById<EditText>(R.id.edNome)

        findViewById<ImageView>(R.id.navigation_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_bosses).setOnClickListener {
            val intent = Intent(this, Bosses::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        if (username != "") {
            edNome.text = Editable.Factory.getInstance().newEditable(username)
            btnConfirmar.text = "Editar"
        }
        consultarUsuarios()

        btnConfirmar.setOnClickListener {
            val nome = edNome.text.toString().trim()
            if (nome.isNotEmpty() && nome.length <= 50) {
                if (username != "") {
                    class UpdateUsuarioTask : AsyncTask<String, Void, Boolean>() {
                        override fun doInBackground(vararg params: String?): Boolean {
                            val nome = params[0]
                            try {
                                val statement = conexao.prepareStatement("UPDATE CotucaClicker.Usuarios SET nome = ?, pontos = ? WHERE nome = ?")
                                statement.setString(1, nome)
                                statement.setLong(2, contador)
                                statement.setString(3, username)
                                statement.executeUpdate()
                                username = edNome.text.toString().trim();
                                return true
                            } catch (e: SQLException) {
                                e.printStackTrace()
                            }
                            return false
                        }
                    }
                    UpdateUsuarioTask().execute(nome)
                } else {
                    class InsertUsuarioTask : AsyncTask<String, Void, Boolean>() {
                        override fun doInBackground(vararg params: String?): Boolean {
                            val nome = params[0]
                            try {
                                val statement = conexao.prepareStatement("INSERT INTO CotucaClicker.Usuarios (nome, pontos) VALUES (?, ?)")
                                statement.setString(1, nome)
                                statement.setLong(2, contador)
                                statement.executeUpdate()
                                username = edNome.text.toString().trim();
                                return true
                            } catch (e: SQLException) {
                                e.printStackTrace()
                            }
                            return false
                        }
                    }
                    InsertUsuarioTask().execute(nome)
                }
                consultarUsuarios();
            } else {
                Toast.makeText(this, "Tamanho não pode ser 0 nem maior que 50", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun consultarUsuarios() {
        class ConsultaUsuariosTask : AsyncTask<Void, Void, ResultSet>() {
            override fun doInBackground(vararg params: Void?): ResultSet? {
                var resultSet: ResultSet? = null
                try {
                    val statement = conexao.createStatement()
                    resultSet = statement.executeQuery("SELECT nome, pontos FROM CotucaClicker.Usuarios ORDER BY pontos DESC")
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                return resultSet
            }

            override fun onPostExecute(result: ResultSet?) {
                super.onPostExecute(result)
                result?.let { resultSet ->
                    preencherTabelaUsuarios(resultSet)
                }
            }
        }

        ConsultaUsuariosTask().execute()
    }

    private fun preencherTabelaUsuarios(resultSet: ResultSet) {
        val tableUsers = findViewById<TableLayout>(R.id.tableUsers)
        val childCount = tableUsers.childCount
        if (childCount > 1) {
            tableUsers.removeViews(1, childCount - 1)
        }

        while (resultSet.next()) {
            val nome = resultSet.getString("nome")
            val pontos = resultSet.getLong("pontos")

            val row = TableRow(this)

            val tvNome = TextView(this)
            tvNome.text = nome
            tvNome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Ajusta o tamanho do texto
            tvNome.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f)
            tvNome.gravity = Gravity.CENTER // Centraliza o texto
            tvNome.setBackgroundResource(R.drawable.table_row_border) // Adiciona borda vermelha

            val tvPontos = TextView(this)
            tvPontos.text = pontos.toString()
            tvPontos.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Ajusta o tamanho do texto
            tvPontos.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f)
            tvPontos.gravity = Gravity.CENTER // Centraliza o texto
            tvPontos.setBackgroundResource(R.drawable.table_row_border) // Adiciona borda vermelha

            row.addView(tvNome)
            row.addView(tvPontos)

            tableUsers.addView(row)
        }
    }

}