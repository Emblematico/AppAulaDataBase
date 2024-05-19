package com.example.appauladatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.appauladatabase.roomDB.Pessoa
import com.example.appauladatabase.roomDB.PessoaDataBase
import com.example.appauladatabase.ui.theme.AppAulaDataBaseTheme
import com.example.appauladatabase.viewModel.PessoaViewModel
import com.example.appauladatabase.viewModel.Repository

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            PessoaDataBase::class.java,
            "pessoa.db"
        ).build()
    }

    private val viewModel by viewModels<PessoaViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PessoaViewModel(Repository(db)) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppAulaDataBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(viewModel, this)
                }
            }
        }
    }
}

@Composable
fun App(viewModel: PessoaViewModel, mainActivity: MainActivity) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var pessoaEditando by remember { mutableStateOf<Pessoa?>(null) }

    var pessoaList by remember { mutableStateOf(listOf<Pessoa>()) }

    viewModel.getPessoa().observe(mainActivity) {
        pessoaList = it
    }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "App DataBase",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text(text = "Nome:") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = telefone,
                onValueChange = { telefone = it },
                label = { Text(text = "Telefone:") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val pessoa = pessoaEditando?.copy(nome = nome, telefone = telefone) ?: Pessoa(nome, telefone)
                    viewModel.upsertPessoa(pessoa)
                    nome = ""
                    telefone = ""
                    pessoaEditando = null
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = if (pessoaEditando == null) "Cadastrar" else "Atualizar")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (pessoaList.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Nome")
                    Text(text = "Telefone")
                    Text(text = "Editar")
                    Text(text = "Apagar")
                }

                pessoaList.forEach { pessoa ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = pessoa.nome)
                        Text(text = pessoa.telefone)
                        IconButton(
                            onClick = {
                                nome = pessoa.nome
                                telefone = pessoa.telefone
                                pessoaEditando = pessoa
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(
                            onClick = {
                                viewModel.deletePessoa(pessoa)
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Apagar")
                        }
                    }
                }
            }
        }
    }
}