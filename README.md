# Aplicativo APOD (Astronomy Picture of the Day)

Este é um aplicativo desenvolvido para consumir a API **APOD (Astronomy Picture of the Day)** da NASA, exibindo a "Foto Astronômica do Dia" junto com seu título e descrição. O projeto também oferece a possibilidade de navegar entre dias anteriores e posteriores para explorar fotos passadas e futuras (quando disponíveis).

## 🛰️ Funcionalidades

- Exibir a foto astronômica do dia ao iniciar o aplicativo.
- Navegar entre fotos de dias anteriores e posteriores usando botões.
- Tratar erros de forma apropriada, incluindo:
  - Mensagem para conexões indisponíveis.
  - Mensagem quando o usuário tenta acessar uma data futura sem dados disponíveis.

## 🔧 Tecnologias Utilizadas

- **Retrofit**: Para realizar as requisições à API da NASA.
- **Java**: Linguagem principal do projeto.
- **LocalDate**: Para manipulação de datas e ajustar os dias.

## 🌌 API Utilizada

- **Astronomy Picture of the Day (APOD)**  
  Endpoint da API: `https://api.nasa.gov/planetary/apod`  
  Para obter a sua chave de acesso, visite: [API Key da NASA](https://api.nasa.gov/index.html)

### Exemplo de Requisição
- Foto do dia atual:
`https://api.nasa.gov/planetary/apod?api_key=SUA_API_KEY`

- Foto de um dia específico:
`https://api.nasa.gov/planetary/apod?api_key=SUA_API_KEY&date=AAAA-MM-DD`


## 📋 Requisitos Funcionais

### Pontos Avaliativos
1. **(5,0p)** Exibir na tela inicial o título, a foto e a explicação retornados pela API.
2. **(4,0p)** Permitir a navegação entre dias passados e futuros usando botões.
3. **(1,0p)** Exibir mensagens de erro apropriadas em casos como:
 - Tentativa de acessar uma data futura.
 - Ausência de conexão com a Internet.

## 🖼️ Interface do Usuário

- Tela inicial com:
- **Título da foto**
- **Imagem astronômica**
- **Descrição da imagem**
- Dois botões:
- **Anterior**: Carregar a foto do dia anterior.
- **Próximo**: Carregar a foto do próximo dia (caso disponível).

## 📚 Adicional

- Tradução dos textos para PT-BR utilizando o sdk `mlkitTranslate`