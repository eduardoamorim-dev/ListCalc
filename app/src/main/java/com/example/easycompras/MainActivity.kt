package com.example.easycompras

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycompras.ui.theme.EasyComprasTheme
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

data class Produto(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val precoUnitario: Double,
    val quantidade: Double
) {
    val valorTotal: Double
        get() = precoUnitario * quantidade
}

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = getSharedPreferences("EasyCompras", Context.MODE_PRIVATE)

        setContent {
            EasyComprasTheme(darkTheme = true) {
                EasyComprasApp()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EasyComprasApp() {
        var produtos by remember { mutableStateOf(carregarProdutos()) }
        var mostrarDialog by remember { mutableStateOf(false) }
        var produtoParaEditar by remember { mutableStateOf<Produto?>(null) }
        var textoBusca by remember { mutableStateOf("") }

        val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        // Filtra produtos baseado na busca
        val produtosFiltrados = remember(produtos, textoBusca) {
            if (textoBusca.isBlank()) {
                produtos
            } else {
                produtos.filter {
                    it.nome.contains(textoBusca, ignoreCase = true)
                }
            }
        }

        // Cores premium dark
        val primaryColor = Color(0xFF00D4AA)
        val gradientColors = listOf(
            Color(0xFF0F0F23),
            Color(0xFF1A1A2E)
        )
        val cardColor = Color(0xFF16213E)
        val surfaceColor = Color(0xFF0F3460)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF0A0A0A),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(primaryColor, Color(0xFF00B4D8))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "ListCalc",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        if (produtos.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    produtos = emptyList()
                                    salvarProdutos(produtos)
                                }
                            ) {
                                Icon(
                                    Icons.Default.DeleteSweep,
                                    contentDescription = "Limpar tudo",
                                    tint = Color(0xFFFF6B6B)
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { mostrarDialog = true },
                    containerColor = primaryColor,
                    contentColor = Color.Black,
                    elevation = FloatingActionButtonDefaults.elevation(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Adicionar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Adicionar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(gradientColors)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(20.dp)
                ) {
                    // Card com resumo premium
                    AnimatedVisibility(
                        visible = produtos.isNotEmpty(),
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardColor
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 16.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                cardColor,
                                                surfaceColor
                                            )
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(28.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "Total da Compra",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        formatter.format(produtos.sumOf { it.valorTotal }),
                                        color = primaryColor,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = primaryColor.copy(alpha = 0.1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Inventory,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "${produtos.size} ${if (produtos.size == 1) "produto" else "produtos"}",
                                                color = primaryColor,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Campo de busca
                    AnimatedVisibility(
                        visible = produtos.isNotEmpty(),
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        OutlinedTextField(
                            value = textoBusca,
                            onValueChange = { textoBusca = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            placeholder = {
                                Text(
                                    "Buscar produto...",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = primaryColor
                                )
                            },
                            trailingIcon = {
                                if (textoBusca.isNotEmpty()) {
                                    IconButton(
                                        onClick = { textoBusca = "" }
                                    ) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Limpar busca",
                                            tint = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedContainerColor = cardColor.copy(alpha = 0.5f),
                                unfocusedContainerColor = cardColor.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Lista de produtos ou estado vazio
                    if (produtos.isEmpty()) {
                        EmptyState()
                    } else if (produtosFiltrados.isEmpty()) {
                        // Nenhum resultado na busca
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Nenhum produto encontrado",
                                    fontSize = 18.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(produtosFiltrados) { _, produto ->
                                val index = produtos.indexOf(produto)
                                ProdutoCard(
                                    produto = produto,
                                    onEdit = { produtoParaEditar = it },
                                    onDelete = {
                                        produtos = produtos.toMutableList().apply {
                                            removeAt(index)
                                        }
                                        salvarProdutos(produtos)
                                    },
                                    onQuantityChange = { newQuantity ->
                                        if (newQuantity >= 0) {
                                            produtos = produtos.map {
                                                if (it.id == produto.id) it.copy(quantidade = newQuantity)
                                                else it
                                            }
                                            salvarProdutos(produtos)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dialog para adicionar/editar produto
        if (mostrarDialog || produtoParaEditar != null) {
            ProdutoDialog(
                produto = produtoParaEditar,
                onDismiss = {
                    mostrarDialog = false
                    produtoParaEditar = null
                },
                onConfirm = { produto ->
                    if (produtoParaEditar != null) {
                        // Editando
                        produtos = produtos.map {
                            if (it.id == produtoParaEditar!!.id) produto
                            else it
                        }
                    } else {
                        // Adicionando
                        produtos = produtos + produto
                    }
                    salvarProdutos(produtos)
                    mostrarDialog = false
                    produtoParaEditar = null
                }
            )
        }
    }

    @Composable
    fun EmptyState() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF16213E),
                                    Color(0xFF0F3460)
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color(0xFF00D4AA),
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Sua lista está vazia",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Adicione produtos à sua lista\npara começar suas compras",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProdutoCard(
        produto: Produto,
        onEdit: (Produto) -> Unit,
        onDelete: () -> Unit,
        onQuantityChange: (Double) -> Unit
    ) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit(produto) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF16213E)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header com nome e ações
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        produto.nome,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        IconButton(
                            onClick = { onEdit(produto) }
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = Color(0xFF00D4AA)
                            )
                        }

                        IconButton(
                            onClick = onDelete
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remover",
                                tint = Color(0xFFFF6B6B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preço unitário
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F3460)
                ) {
                    Text(
                        "Preço unitário: ${formatter.format(produto.precoUnitario)}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controle de quantidade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Quantidade:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Botão menos
                        Surface(
                            onClick = {
                                if (produto.quantidade > 0) {
                                    onQuantityChange(produto.quantidade - 1)
                                }
                            },
                            shape = CircleShape,
                            color = if (produto.quantidade > 0) Color(0xFFFF6B6B) else Color(0xFF333333),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Diminuir",
                                    tint = if (produto.quantidade > 0) Color.White else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Quantidade atual
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF00D4AA).copy(alpha = 0.2f)
                        ) {
                            Text(
                                "${produto.quantidade.toInt()}",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00D4AA)
                            )
                        }

                        // Botão mais
                        Surface(
                            onClick = { onQuantityChange(produto.quantidade + 1) },
                            shape = CircleShape,
                            color = Color(0xFF00D4AA),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Aumentar",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Total do item
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        formatter.format(produto.valorTotal),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00D4AA)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProdutoDialog(
        produto: Produto?,
        onDismiss: () -> Unit,
        onConfirm: (Produto) -> Unit
    ) {
        var nome by remember { mutableStateOf(produto?.nome ?: "") }
        var precoText by remember { mutableStateOf(produto?.precoUnitario?.let { if (it > 0) it.toString() else "" } ?: "") }
        var quantidadeText by remember { mutableStateOf(produto?.quantidade?.let { if (it > 0) it.toString() else "" } ?: "") }

        val isEditing = produto != null

        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(24.dp),
            containerColor = Color(0xFF16213E),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF00D4AA)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        if (isEditing) "Editar Produto" else "Novo Produto",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome do produto", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00D4AA),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Label, contentDescription = null, tint = Color(0xFF00D4AA))
                        }
                    )

                    OutlinedTextField(
                        value = precoText,
                        onValueChange = {
                            if (it.matches(Regex("^\\d*[.]?\\d*$"))) {
                                precoText = it
                            }
                        },
                        label = { Text("Preço unitário (R$) - Opcional", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("0", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00D4AA),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF00D4AA))
                        }
                    )

                    OutlinedTextField(
                        value = quantidadeText,
                        onValueChange = {
                            if (it.matches(Regex("^\\d*[.]?\\d*$"))) {
                                quantidadeText = it
                            }
                        },
                        label = { Text("Quantidade - Opcional", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("0", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00D4AA),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Numbers, contentDescription = null, tint = Color(0xFF00D4AA))
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val preco = precoText.toDoubleOrNull() ?: 0.0
                        val quantidade = quantidadeText.toDoubleOrNull() ?: 0.0

                        if (nome.isNotBlank()) {
                            onConfirm(
                                Produto(
                                    id = produto?.id ?: UUID.randomUUID().toString(),
                                    nome = nome.trim(),
                                    precoUnitario = preco,
                                    quantidade = quantidade
                                )
                            )
                        }
                    },
                    enabled = nome.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D4AA),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        if (isEditing) "Salvar" else "Adicionar",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Cancelar",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        )
    }

    private fun salvarProdutos(produtos: List<Produto>) {
        val jsonArray = JSONArray()
        produtos.forEach { produto ->
            val jsonObject = JSONObject().apply {
                put("id", produto.id)
                put("nome", produto.nome)
                put("precoUnitario", produto.precoUnitario)
                put("quantidade", produto.quantidade)
            }
            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit().putString("produtos", jsonArray.toString()).apply()
    }

    private fun carregarProdutos(): List<Produto> {
        val json = sharedPreferences.getString("produtos", null)
        return if (json != null) {
            try {
                val jsonArray = JSONArray(json)
                val produtos = mutableListOf<Produto>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    produtos.add(
                        Produto(
                            id = jsonObject.optString("id", UUID.randomUUID().toString()),
                            nome = jsonObject.getString("nome"),
                            precoUnitario = jsonObject.getDouble("precoUnitario"),
                            quantidade = jsonObject.getDouble("quantidade")
                        )
                    )
                }
                produtos
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}