# ListCalc

## Descrição

ListCalc é uma aplicação Android desenvolvida em Kotlin utilizando Jetpack Compose para gerenciamento de listas de compras com cálculo automático de valores. A aplicação permite aos usuários adicionar, editar e remover produtos, calculando automaticamente o valor total da compra.

## Funcionalidades

- **Adicionar Produtos**: Inserção de produtos com nome, preço unitário e quantidade
- **Editar Produtos**: Modificação de produtos existentes na lista
- **Remover Produtos**: Exclusão de produtos da lista de compras
- **Busca de Produtos**: Filtro de produtos por nome
- **Cálculo Automático**: Cálculo automático do valor total da compra
- **Persistência de Dados**: Armazenamento local usando SharedPreferences
- **Interface Moderna**: UI desenvolvida com Jetpack Compose e tema dark premium

## Demo

<p align="center">
  <img src="https://github.com/user-attachments/assets/72a914a5-2913-4d20-ab83-53dae21d2b89" alt="Imagem 1" width="30%">
  <img src="https://github.com/user-attachments/assets/5c564a9b-68ef-4fd5-aa6c-1dc4d45dcfa8" alt="Imagem 2" width="30%">
  <img src="https://github.com/user-attachments/assets/0c38b794-fe6a-4a94-a5e2-60564b6f4288" alt="Imagem 3" width="30%">
</p>


## Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **Framework UI**: Jetpack Compose
- **Persistência**: SharedPreferences
- **Arquitetura**: MVVM com Compose State Management
- **Build Tool**: Gradle com Kotlin DSL
- **Versão Android**: API 24+ (Android 7.0)

## Estrutura do Projeto

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/easycompras/
│   │   │   ├── MainActivity.kt          # Atividade principal
│   │   │   └── ui/theme/               # Configurações de tema
│   │   ├── res/                        # Recursos da aplicação
│   │   └── AndroidManifest.xml        # Manifesto da aplicação
│   ├── test/                          # Testes unitários
│   └── androidTest/                   # Testes instrumentados
├── build.gradle.kts                   # Configurações de build
└── proguard-rules.pro                # Regras de ofuscação
```

## Modelo de Dados

### Classe Produto
```kotlin
data class Produto(
    val id: String,
    val nome: String,
    val precoUnitario: Double,
    val quantidade: Double
) {
    val valorTotal: Double
        get() = precoUnitario * quantidade
}
```

## Pré-requisitos

- Android Studio Arctic Fox ou superior
- JDK 8 ou superior
- SDK Android 24 ou superior
- Dispositivo Android ou emulador com API 24+

## Como Executar

1. Clone o repositório
```bash
git clone <url-do-repositorio>
```

2. Abra o projeto no Android Studio

3. Sincronize as dependências do Gradle

4. Execute a aplicação em um dispositivo ou emulador Android

## Funcionalidades Detalhadas

### Gerenciamento de Produtos
- Interface intuitiva para adicionar novos produtos
- Validação de campos obrigatórios
- Formatação automática de valores monetários em Real (R$)

### Persistência Local
- Salvamento automático da lista de produtos
- Carregamento dos dados ao iniciar a aplicação
- Utilização de JSON para serialização dos dados

### Interface do Usuário
- Design moderno com tema dark
- Animações suaves entre transições
- Layout responsivo adaptável a diferentes tamanhos de tela
- Cores premium com gradientes

## Estrutura de Cores

- **Cor Primária**: #00D4AA (Verde-água)
- **Background**: #0A0A0A (Preto profundo)
- **Cards**: #16213E (Azul escuro)
- **Surface**: #0F3460 (Azul médio)


## Autor

Eduardo Amorim

---

**Nota**: Este projeto foi desenvolvido como parte de uma avaliação acadêmica, demonstrando conhecimentos em desenvolvimento Android moderno com Kotlin e Jetpack Compose.
