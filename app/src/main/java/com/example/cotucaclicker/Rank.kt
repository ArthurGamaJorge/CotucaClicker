package com.example.cotucaclicker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.Image
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
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
import android.util.TypedValue


class Rank : MainActivity() {

    private lateinit var edNome : EditText
    private lateinit var btnConfirmar : Button
    private lateinit var lbcarregando : TextView
    private lateinit var btnReload : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)
        btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        edNome = findViewById<EditText>(R.id.edNome)
        lbcarregando = findViewById<TextView>(R.id.lbcarregando)
        btnReload = findViewById<ImageView>(R.id.btnReload)

        findViewById<ImageView>(R.id.navigation_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_bosses).setOnClickListener {
            val intent = Intent(this, Bosses::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_loja).setOnClickListener {
            val intent = Intent(this, Loja::class.java)
            startActivity(intent)
        }
        if (username != "") {
            edNome.text = Editable.Factory.getInstance().newEditable(username)
            btnConfirmar.text = "Editar"
        }
        edNome.filters = arrayOf(criarFiltroEntrada())

        consultarUsuarios()

        btnReload.setOnClickListener {
                class AtualizarPontosTask : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg params: Void?): Void? {
                        try {
                            val statement =
                                conexao.prepareStatement("UPDATE CotucaClicker.Usuario SET pontos = ? WHERE nome = ?")
                            statement.setLong(1, contador)
                            statement.setString(2, username)
                            statement.executeUpdate()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return null
                    }
                }
                AtualizarPontosTask().execute()
                consultarUsuarios()
        }

        btnConfirmar.setOnClickListener {
            val nome = edNome.text.toString().trim()
            if (nome.isNotEmpty() && nome.length <= 50) {
                if (username != nome) {
                    class VerificarNomeUsuarioTask : AsyncTask<String, Void, Boolean>() {
                        override fun doInBackground(vararg params: String?): Boolean {
                            val nome = params[0]
                            try {
                                val statement =
                                    conexao.prepareStatement("SELECT COUNT(*) AS count FROM CotucaClicker.Usuario WHERE nome = ?")
                                statement.setString(1, nome)
                                val resultSet = statement.executeQuery()
                                if (resultSet.next()) {
                                    val count = resultSet.getInt("count")
                                    return count == 0
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            return false
                        }

                        override fun onPostExecute(result: Boolean) {
                            super.onPostExecute(result)
                            if (result) {
                                if (username != "") {
                                    class UpdateUsuarioTask : AsyncTask<String, Void, Boolean>() {
                                        override fun doInBackground(vararg params: String?): Boolean {
                                            val nome = params[0]
                                            try {
                                                val statement =
                                                    conexao.prepareStatement("UPDATE CotucaClicker.Usuario SET nome = ?, pontos = ? WHERE nome = ?")
                                                statement.setString(1, nome)
                                                statement.setLong(2, contador)
                                                statement.setString(3, username)
                                                statement.executeUpdate()
                                                username = nome
                                                return true
                                            } catch (e: Exception) {
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
                                                val statement =
                                                    conexao.prepareStatement("INSERT INTO CotucaClicker.Usuario (nome, pontos) VALUES (?, ?)")
                                                statement.setString(1, nome)
                                                statement.setLong(2, contador)
                                                statement.executeUpdate()
                                                username = nome
                                                return true
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            return false
                                        }
                                    }
                                    InsertUsuarioTask().execute(nome)
                                }
                                consultarUsuarios()
                            } else {
                                Toast.makeText(
                                    this@Rank,
                                    "O nome de usuário já está em uso.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    VerificarNomeUsuarioTask().execute(nome)
                } else {
                    Toast.makeText(
                        this@Rank,
                        "Nenhum alteração de nome de usuário foi feita.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@Rank,
                    "O tamanho do nome de usuário deve estar entre 1 e 50 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun criarFiltroEntrada(): InputFilter {
        return object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                for (i in start until end) {
                    if (!Character.isLetterOrDigit(source?.get(i) ?: ' ')) {
                        return ""
                    }
                }
                return null
            }
        }
    }

    private fun consultarUsuarios() {
        lbcarregando.text = "Carregando..."
        class ConsultaUsuariosTask : AsyncTask<Void, Void, ResultSet>() {
            override fun doInBackground(vararg params: Void?): ResultSet? {
                var resultSet: ResultSet? = null
                try {
                    val statement = conexao.createStatement()
                    resultSet = statement.executeQuery("SELECT nome, pontos FROM CotucaClicker.Usuario ORDER BY pontos DESC")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return resultSet
            }

            override fun onPostExecute(result: ResultSet?) {
                super.onPostExecute(result)
                result?.let { resultSet ->
                    preencherTabelaUsuarios(resultSet)
                }
                lbcarregando.text = ""
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
            val pontos = resultSet.getLong("pontos").formatarComPontos()

            val row = TableRow(this)

            val tvNome = TextView(this)
            tvNome.text = nome
            tvNome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Ajusta o tamanho do texto
            tvNome.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f) // Usando WEIGHT para distribuição igual
            tvNome.gravity = Gravity.CENTER // Centraliza o texto
            tvNome.maxLines = 1 // Define o número máximo de linhas para 1
            tvNome.setBackgroundResource(R.drawable.table_row_border) // Adiciona borda vermelha

            val tvPontos = TextView(this)
            tvPontos.text = pontos.toString()
            tvPontos.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Ajusta o tamanho do texto
            tvPontos.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f) // Usando WEIGHT para distribuição igual
            tvPontos.gravity = Gravity.CENTER // Centraliza o texto
            tvPontos.setBackgroundResource(R.drawable.table_row_border) // Adiciona borda vermelha

            row.addView(tvNome)
            row.addView(tvPontos)

            tableUsers.addView(row)
        }
    }

}