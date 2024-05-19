package com.example.appauladatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    // Variável global para pessoaEditando
    private var pessoaEditando by mutableStateOf<Pessoa?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppAulaDataBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "menu") {
                        composable("menu") { MenuScreen(navController) }
                        composable("cadastro") { CadastroScreen(viewModel) }
                        composable("visualizacao") {
                            VisualizacaoScreen(
                                viewModel,
                                navController
                            )
                        }
                        composable("edicao") { // Adicione esta linha para a tela de edição
                            EdicaoScreen(
                                viewModel,
                                pessoaEditando,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun MenuScreen(navController: NavHostController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Menu",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.padding(20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("cadastro") }) {
                Text(text = "Cadastrar Pessoa")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("visualizacao") }) {
                Text(text = "Visualizar Pessoas")
            }
        }
    }

    @Composable
    fun CadastroScreen(viewModel: PessoaViewModel) {
        var nome by remember { mutableStateOf("") }
        var telefone by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Cadastro de Pessoa",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
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
                        val pessoa = Pessoa(nome, telefone)
                        viewModel.upsertPessoa(pessoa)
                        nome = ""
                        telefone = ""
                        pessoaEditando = null
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Cadastrar")
                }
            }
        }
    }

    @Composable
    fun EdicaoScreen(
        viewModel: PessoaViewModel,
        pessoaEditando: Pessoa?,
        onNavigateBack: () -> Unit
    ) {
        var nome by remember { mutableStateOf(pessoaEditando?.nome ?: "") }
        var telefone by remember { mutableStateOf(pessoaEditando?.telefone ?: "") }

        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Edição de Pessoa",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            val pessoa = pessoaEditando?.copy(nome = nome, telefone = telefone) ?: Pessoa(nome, telefone)
                            viewModel.upsertPessoa(pessoa)
                            onNavigateBack()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "Salvar")
                    }
                    Button(
                        onClick = onNavigateBack
                    ) {
                        Text(text = "Cancelar")
                    }
                }
            }
        }
    }

    @Composable
    fun VisualizacaoScreen(viewModel: PessoaViewModel, navController: NavHostController) {
        var pessoaList by remember { mutableStateOf(listOf<Pessoa>()) }

        viewModel.getPessoa().observeForever {
            pessoaList = it
        }

        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Visualização de Pessoas",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            )

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
                        Text(text = "Nome", color = Color.White)
                        Text(text = "Telefone", color = Color.White)
                        Text(text = "Editar", color = Color.White)
                        Text(text = "Apagar", color = Color.White)
                    }

                    pessoaList.forEach { pessoa ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = pessoa.nome, color = Color.White)
                            Text(text = pessoa.telefone, color = Color.White)
                            IconButton(
                                onClick = {
                                    pessoaEditando = pessoa
                                    navController.navigate("edicao")
                                }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.deletePessoa(pessoa)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Apagar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
