# 🧪 Plano de Testes Funcionais — Big Chat Brasil

## 📋 Objetivo

Este documento orienta a validação funcional completa da aplicação Big Chat Brasil, incluindo:

- autenticação e encerramento de sessão;
- autorização por perfil;
- cadastro e manutenção de clientes e contatos;
- conversas e mensagens;
- fila persistente, prioridade e ordem de processamento;
- cobrança de clientes pré-pagos e pós-pagos;
- histórico financeiro;
- cenários de sucesso, validação e erro;
- comportamento visual, responsividade e experiência de uso do frontend.

## 🧭 Escopo disponível no frontend

O frontend permite testar diretamente:

- autenticação de clientes;
- consulta de perfil, saldo ou limite disponível;
- listagem e pesquisa de conversas;
- abertura de conversa com um contato já cadastrado;
- consulta do histórico de mensagens;
- envio de mensagens normais e urgentes;
- atualização automática do estado das mensagens;
- marcação automática de mensagens recebidas como lidas;
- encerramento da sessão;
- responsividade da interface.

As seguintes funcionalidades existem somente na API e devem ser testadas com Postman, Insomnia ou `curl.exe`:

- cadastro de clientes;
- cadastro e atualização de contatos;
- consulta do histórico financeiro;
- simulação de mensagem recebida;
- administração de clientes;
- adição de crédito pré-pago;
- ajuste do limite pós-pago;
- alteração e redefinição de senha.

## 🛠️ Preparação do ambiente

### ▶️ Inicialização

Na raiz do projeto, execute no PowerShell:

```powershell
Copy-Item .env.example .env
docker compose up --build -d
docker compose ps
```

Endereços esperados:

- frontend: `http://localhost:4200`;
- API: `http://localhost:8080`;
- PostgreSQL: `localhost:5432`.

Todos os serviços exibidos por `docker compose ps` devem permanecer em execução. O PostgreSQL deve apresentar estado saudável.

### 🧹 Restauração dos dados iniciais

Para apagar os dados criados durante os testes e executar novamente as migrações e cargas iniciais:

```powershell
docker compose down -v
docker compose up --build -d
```

⚠️ O parâmetro `-v` remove definitivamente o volume local do PostgreSQL.

### 🔍 Acompanhamento dos registros

```powershell
docker compose logs -f backend
```

Não devem ocorrer exceções inesperadas durante os fluxos aprovados.

## 👥 Usuários e dados iniciais

| Perfil | Documento | Senha | Condição inicial |
|---|---|---|---|
| Administrador | `11222333000181` | `Admin@123` | Acesso às operações administrativas |
| Cliente pré-pago | `52998224725` | `Client@123` | Saldo de R$ 50,00 |
| Cliente pós-pago | `11144477735` | `Client@123` | Limite de R$ 100,00 e uso de R$ 0,50 |
| Cliente pós-pago CNPJ | `11444777000161` | `Client@123` | Possui mensagens em fila e com falha |

Contatos iniciais:

| Nome | Contato | Tipo |
|---|---|---|
| Marina Souza | `+5511999990001` | Telefone |
| Lucas Almeida | `+5511999990002` | Telefone |
| Finance Department | `finance@example.com` | E-mail |

## 📸 Registro das evidências

Para cada caso de teste, registre:

- identificador e nome do cenário;
- data e responsável pela execução;
- navegador e resolução utilizados;
- dados de entrada;
- resultado esperado;
- resultado obtido;
- método, endereço e status HTTP;
- corpo enviado e resposta recebida;
- captura de tela;
- registros relevantes do backend;
- situação final: aprovado, reprovado ou bloqueado.

No navegador, pressione `F12` e utilize `Network` → `Fetch/XHR`. Ative a preservação dos registros para acompanhar redirecionamentos e logout.

## 🔐 Testes de autenticação pelo frontend

### ✅ AUT-001 — Login pré-pago válido

1. Acesse `http://localhost:4200/login`.
2. Informe `52998224725` e `Client@123`.
3. Clique em `Entrar na plataforma`.

Resultado esperado:

- `POST /api/auth` retorna `200 OK`;
- resposta contém token, validade, identificador, nome e perfil;
- usuário é direcionado para `/chat`;
- `GET /api/clients/me` retorna `200 OK`;
- saldo apresentado é R$ 50,00.

### ✅ AUT-002 — Login pós-pago válido

1. Informe `11144477735` e `Client@123`.
2. Efetue o login.

Resultado esperado:

- autenticação retorna `200 OK`;
- limite disponível é R$ 99,50;
- uso mensal é R$ 0,50.

### ❌ AUT-003 — Senha incorreta

1. Informe um documento cadastrado.
2. Use uma senha incorreta com pelo menos oito caracteres.

Resultado esperado:

- `POST /api/auth` retorna `401 Unauthorized`;
- interface apresenta `Documento ou senha inválidos. Confira os dados e tente novamente.`;
- nenhuma sessão é armazenada;
- usuário permanece em `/login`.

### ❌ AUT-004 — Documento inexistente

Use um CPF válido que não esteja cadastrado.

Resultado esperado:

- `POST /api/auth` retorna `401 Unauthorized`;
- aplicação não informa se o documento existe, evitando enumeração de usuários.

### ⚠️ AUT-005 — Campos vazios

1. Não preencha os campos.
2. Clique em entrar.

Resultado esperado:

- mensagens de validação são exibidas;
- nenhuma requisição HTTP é enviada.

### ⚠️ AUT-006 — Documento curto

Informe menos de 11 caracteres no documento.

Resultado esperado:

- mensagem `Informe um documento válido.`;
- nenhuma requisição HTTP é enviada.

### ⚠️ AUT-007 — Senha curta

Informe uma senha com menos de oito caracteres.

Resultado esperado:

- mensagem sobre o tamanho mínimo da senha;
- nenhuma requisição HTTP é enviada.

### 🛡️ AUT-008 — Acesso sem sessão

1. Abra uma janela anônima.
2. Acesse diretamente `http://localhost:4200/chat`.

Resultado esperado:

- redirecionamento local para `/login`;
- conteúdo protegido não é exibido.

### 🚪 AUT-009 — Logout

1. Autentique-se.
2. Clique no botão de saída.

Resultado esperado:

- `POST /api/auth/logout` retorna `204 No Content`;
- sessão é removida do armazenamento local;
- usuário volta para `/login`;
- tentativa posterior de usar o token revogado em endpoint protegido retorna `403 Forbidden`.

## 💬 Testes de conversas pelo frontend

### ✅ CON-001 — Listar conversas

Após o login, confirme:

- `GET /api/conversations?size=100` retorna `200 OK`;
- conversas pertencem somente ao cliente autenticado;
- lista apresenta nome, contato, última mensagem, horário e quantidade de não lidas;
- ordenação acompanha a atualização mais recente.

### 🔎 CON-002 — Pesquisar conversa

Pesquise por nome e depois por parte do contato.

Resultado esperado:

- lista é filtrada corretamente;
- nenhuma requisição HTTP adicional é realizada;
- pesquisa sem correspondência apresenta estado vazio.

### ✅ CON-003 — Abrir nova conversa

1. Clique no botão `+`.
2. Selecione um contato ativo.
3. Clique em `Abrir conversa`.

Resultado esperado:

- `GET /api/recipients?size=100` retorna `200 OK`;
- somente contatos ativos aparecem;
- `POST /api/conversations` retorna `201 Created`;
- conversa é selecionada automaticamente.

### ♻️ CON-004 — Selecionar contato que já possui conversa

Resultado esperado:

- API retorna a conversa existente com `201 Created`;
- nenhuma conversa duplicada é criada;
- identificador permanece o mesmo.

### ❌ CON-005 — Contato inexistente

Envie pela API um `recipientId` que não exista.

Resultado esperado:

- `POST /api/conversations` retorna `404 Not Found`;
- nenhuma conversa é criada.

### ❌ CON-006 — Contato inativo

Desative o contato como administrador e tente criar uma conversa.

Resultado esperado:

- resposta `404 Not Found`;
- nenhuma conversa é criada.

### 🛡️ CON-007 — Isolamento entre clientes

Use o identificador de uma conversa de outro cliente nos endpoints de mensagens.

Resultado esperado:

- resposta `404 Not Found`;
- nenhuma informação sobre a conversa de outro cliente é revelada.

## ✉️ Testes de mensagens pelo frontend

### ✅ MSG-001 — Consultar histórico

Selecione uma conversa.

Resultado esperado:

- `GET /api/conversations/{conversationId}/messages?size=100` retorna `200 OK`;
- mensagens são exibidas cronologicamente;
- direção, prioridade, horário e estado são representados corretamente.

### ✅ MSG-002 — Enviar mensagem normal

1. Mantenha a prioridade `Normal`.
2. Digite uma mensagem.
3. Clique em enviar.

Resultado esperado:

- `POST /api/conversations/{conversationId}/messages` retorna `201 Created`;
- custo é R$ 0,25;
- estado inicial é `QUEUED`;
- interface apresenta `Na fila`;
- saldo ou uso mensal é atualizado.

### 🚨 MSG-003 — Enviar mensagem urgente

1. Selecione `Urgente`.
2. Envie uma mensagem.

Resultado esperado:

- endpoint retorna `201 Created`;
- prioridade é `URGENT`;
- custo é R$ 0,50;
- indicador visual de urgência é apresentado.

### ⏳ MSG-004 — Atualização do processamento

Aguarde pelo menos três segundos após o envio.

Resultado esperado:

- frontend consulta novamente o histórico;
- estado muda de `Na fila` para `Enviando` ou `Enviada`;
- processamento final apresenta `SENT` e `sentAt` preenchido.

### ⌨️ MSG-005 — Envio com Enter

Pressione Enter sem Shift.

Resultado esperado:

- mensagem é enviada uma única vez;
- nenhuma quebra de linha é criada.

### ↩️ MSG-006 — Quebra de linha com Shift+Enter

Resultado esperado:

- nova linha é adicionada ao texto;
- nenhuma requisição é enviada.

### ⚠️ MSG-007 — Conteúdo vazio

Resultado esperado:

- botão permanece desabilitado;
- nenhuma requisição é enviada.

### ⚠️ MSG-008 — Limite de 2.000 caracteres

Resultado esperado:

- frontend não permite ultrapassar 2.000 caracteres;
- API aceita conteúdo com exatamente 2.000 caracteres;
- conteúdo maior enviado diretamente para a API retorna `400 Bad Request`.

### 💸 MSG-009 — Saldo pré-pago insuficiente

Use um cliente pré-pago recém-cadastrado, cujo saldo inicial é zero.

Resultado esperado:

- envio retorna `402 Payment Required`;
- mensagem informa saldo insuficiente;
- mensagem não é persistida;
- débito não é criado.

### 📊 MSG-010 — Limite pós-pago excedido

Use um cliente pós-pago sem limite disponível.

Resultado esperado:

- envio retorna `402 Payment Required`;
- mensagem informa limite mensal excedido;
- uso mensal não é alterado;
- mensagem e débito não são persistidos.

### ⛔ MSG-011 — Envio para contato inativo

Resultado esperado:

- resposta `422 Unprocessable Entity`;
- mensagem não entra na fila;
- cliente não é cobrado.

### ❌ MSG-012 — Prioridade inválida

Envie diretamente à API uma prioridade diferente de `NORMAL` ou `URGENT`.

Resultado esperado:

- resposta `400 Bad Request`;
- mensagem `Malformed request body`.

## 📥 Testes de mensagens recebidas

### ✅ REC-001 — Simular recebimento

```powershell
curl.exe -i -X POST "$base/api/conversations/{conversationId}/messages/inbound" `
  -H "Authorization: Bearer $clientToken" `
  -H "Content-Type: application/json" `
  -d '{"content":"Mensagem recebida no teste funcional"}'
```

Resultado esperado:

- `201 Created`;
- direção `INBOUND`;
- prioridade `NORMAL`;
- estado `RECEIVED`;
- custo `0.00`;
- `readAt` inicialmente nulo;
- nenhuma transação financeira é criada.

### 👁️ REC-002 — Marcar recebida como lida

Ao abrir a conversa no frontend:

- `PATCH /api/conversations/{conversationId}/messages/{messageId}/read` retorna `200 OK`;
- `readAt` passa a ter data e hora;
- contador de não lidas deve diminuir.

### ❌ REC-003 — Marcar mensagem enviada como lida

Resultado esperado:

- resposta `404 Not Found`;
- mensagem enviada não é alterada.

### ⚠️ REC-004 — Recebimento com conteúdo inválido

Conteúdo vazio ou maior que 2.000 caracteres deve retornar `400 Bad Request`.

## 💰 Testes financeiros

### ✅ FIN-001 — Cobrança pré-paga normal

1. Entre com `52998224725`.
2. Anote o saldo.
3. Envie uma mensagem normal.

Resultado esperado:

- saldo diminui R$ 0,25;
- transação `DEBIT` é criada com valor `-0.25`;
- `balanceAfter` corresponde ao novo saldo.

### 🚨 FIN-002 — Cobrança pré-paga urgente

Resultado esperado:

- saldo diminui R$ 0,50;
- transação `DEBIT` registra `-0.50`.

### ✅ FIN-003 — Consumo pós-pago normal

Use `11144477735`.

Resultado esperado:

- uso mensal passa de R$ 0,50 para R$ 0,75;
- limite disponível diminui na mesma proporção;
- transação `DEBIT` é criada.

### 🚨 FIN-004 — Consumo pós-pago urgente

Após o cenário anterior, o uso mensal deve passar de R$ 0,75 para R$ 1,25.

### 📜 FIN-005 — Histórico financeiro

```powershell
curl.exe -i "$base/api/clients/me/transactions?size=100" `
  -H "Authorization: Bearer $clientToken"
```

Resultado esperado:

- `200 OK`;
- transações ordenadas da mais recente para a mais antiga;
- créditos, débitos e ajustes apresentam valor, descrição e data coerentes.

## 📨 Fila, prioridade e ordem de processamento

### 📴 FIL-001 — Manter mensagens em fila

Defina no `.env`:

```text
APP_MESSAGING_PROCESSING_ENABLED=false
```

Reinicie o backend e envie, nesta ordem:

1. mensagem normal 1;
2. mensagem normal 2;
3. mensagem urgente 1.

Resultado esperado:

- todas permanecem com estado `QUEUED`;
- nenhuma possui `sentAt`;
- todas já possuem cobrança registrada.

### 🚨 FIL-002 — Prioridade urgente

Altere a configuração para `true` e reinicie o backend.

Resultado esperado:

- mensagem urgente 1 é enviada antes das normais;
- `sentAt` da urgente é anterior ao das mensagens normais.

### 🔢 FIL-003 — Ordem FIFO

Resultado esperado:

- mensagem normal 1 é processada antes da normal 2;
- dentro da mesma prioridade, a ordem de criação é preservada.

### ♻️ FIL-004 — Persistência após reinicialização

1. Desative o processador.
2. Envie mensagens.
3. Reinicie o backend sem remover o volume.
4. Ative o processador.

Resultado esperado:

- mensagens continuam armazenadas;
- processamento recomeça sem perda ou duplicação.

## 🧑‍💼 Testes administrativos pela API

### 🔑 Obter token de administrador

```powershell
$base = "http://localhost:8080"

$admin = Invoke-RestMethod -Method Post `
  -Uri "$base/api/auth" `
  -ContentType "application/json" `
  -Body '{"documentId":"11222333000181","password":"Admin@123"}'

$adminToken = $admin.token
```

### ✅ ADM-001 — Listar clientes

```powershell
curl.exe -i "$base/api/admin/clients?size=100" `
  -H "Authorization: Bearer $adminToken"
```

Resultado esperado: `200 OK`.

### ➕ ADM-002 — Cadastrar cliente pré-pago

```powershell
curl.exe -i -X POST "$base/api/register" `
  -H "Content-Type: application/json" `
  -d '{"documentId":"12345678909","documentType":"CPF","name":"Cliente Funcional","password":"Teste@123","planType":"PREPAID"}'
```

Resultado esperado:

- `201 Created`;
- perfil `CLIENT`;
- cliente ativo;
- saldo inicial R$ 0,00.

### ➕ ADM-003 — Cadastrar cliente pós-pago

Use documento válido e não cadastrado com `planType` igual a `POSTPAID`.

Resultado esperado:

- `201 Created`;
- limite e consumo iniciais iguais a zero.

### ❌ ADM-004 — Documento inválido

Resultado esperado: `400 Bad Request`.

### ♻️ ADM-005 — Documento duplicado

Resultado esperado: `409 Conflict`.

### ✏️ ADM-006 — Atualizar cliente

Resultado esperado:

- `PUT /api/admin/clients/{id}` retorna `200 OK`;
- nome e situação ativa são atualizados.

### 🔒 ADM-007 — Redefinir senha

Resultado esperado:

- `PUT /api/admin/clients/{id}/password` retorna `204 No Content`;
- senha anterior deixa de autenticar;
- senha nova retorna `200 OK` no login.

### 💳 ADM-008 — Adicionar crédito pré-pago

```powershell
curl.exe -i -X POST "$base/api/admin/clients/{clientId}/credits" `
  -H "Authorization: Bearer $adminToken" `
  -H "Content-Type: application/json" `
  -d '{"amount":10.00}'
```

Resultado esperado:

- `201 Created`;
- saldo aumenta em R$ 10,00;
- transação `CREDIT` é registrada.

Erros esperados:

- valor zero ou negativo: `400 Bad Request`;
- cliente inexistente: `404 Not Found`;
- cliente pós-pago: `409 Conflict`;
- token de cliente comum: `403 Forbidden`.

### 📈 ADM-009 — Ajustar limite pós-pago

```powershell
curl.exe -i -X PUT "$base/api/admin/clients/{clientId}/monthly-limit" `
  -H "Authorization: Bearer $adminToken" `
  -H "Content-Type: application/json" `
  -d '{"amount":150.00}'
```

Resultado esperado:

- `200 OK`;
- novo limite é R$ 150,00;
- transação `LIMIT_ADJUSTMENT` é criada.

Erros esperados:

- valor zero ou negativo: `400 Bad Request`;
- cliente inexistente: `404 Not Found`;
- cliente pré-pago: `409 Conflict`;
- token de cliente comum: `403 Forbidden`.

## 📇 Testes de contatos pela API

### ➕ CTD-001 — Cadastrar contato

```powershell
curl.exe -i -X POST "$base/api/recipients" `
  -H "Authorization: Bearer $adminToken" `
  -H "Content-Type: application/json" `
  -d '{"name":"Contato Funcional","contact":"+5511999999998","contactType":"PHONE"}'
```

Resultado esperado: `201 Created` e contato ativo.

### ♻️ CTD-002 — Contato duplicado

Cadastre novamente o mesmo contato e tipo.

Resultado esperado: `409 Conflict`.

### ⚠️ CTD-003 — Dados inválidos

| Entrada | Resultado esperado |
|---|---|
| Nome vazio | `400 Bad Request` |
| Contato vazio | `400 Bad Request` |
| Nome maior que 150 caracteres | `400 Bad Request` |
| Contato maior que 150 caracteres | `400 Bad Request` |
| Tipo diferente de `PHONE` ou `EMAIL` | `400 Bad Request` |

### 📴 CTD-004 — Desativar contato

Use `PUT /api/recipients/{id}` como administrador.

Resultado esperado:

- `200 OK`;
- `active` igual a `false`;
- contato deixa de aparecer na seleção do frontend;
- novas mensagens para conversa existente retornam `422 Unprocessable Entity`.

## 🔑 Testes de alteração de senha

### ✅ SEN-001 — Alterar a própria senha

Envie a senha atual e uma nova senha válida para `PUT /api/clients/me/password`.

Resultado esperado:

- `204 No Content`;
- senha anterior passa a retornar `401 Unauthorized` no login;
- senha nova autentica com `200 OK`.

### ❌ SEN-002 — Senha atual incorreta

Resultado esperado: `400 Bad Request` e nenhuma alteração.

### ⚠️ SEN-003 — Nova senha inválida

Senha menor que 8 ou maior que 72 caracteres deve retornar `400 Bad Request`.

## 🛡️ Testes de autorização

| Cenário | Resultado esperado |
|---|---|
| Endpoint protegido sem token | `403 Forbidden` |
| Token inexistente, inválido, expirado ou revogado | `403 Forbidden` |
| Cliente acessando `/api/admin/**` | `403 Forbidden` |
| Administrador acessando endpoints exclusivos de conversa de cliente | `403 Forbidden` |
| Cliente consultando conversa de outro cliente | `404 Not Found` |
| Token pertencente a cliente desativado | `403 Forbidden` |

## 📱 Testes de UI e UX

### 📐 UIX-001 — Responsividade

Execute em pelo menos:

- 375 × 667;
- 768 × 1024;
- 1440 × 900.

Resultado esperado:

- ausência de rolagem horizontal indevida;
- campos e botões acessíveis;
- painel de conversa utilizável em telas pequenas;
- botão de voltar fecha a conversa em dispositivos móveis;
- modal permanece dentro da área visível.

### ♿ UIX-002 — Navegação por teclado

Resultado esperado:

- ordem de foco acompanha a ordem visual;
- login pode ser realizado sem mouse;
- conversa e prioridade podem ser selecionadas pelo teclado;
- foco é visível;
- Enter e Shift+Enter respeitam os comportamentos definidos.

### 🔔 UIX-003 — Estados e retornos visuais

Validar:

- esqueletos durante carregamento;
- indicador durante login e envio;
- botões desabilitados durante operações;
- estado vazio de conversas;
- mensagem de erro com função de fechar;
- identificação visual de mensagem urgente;
- estados `Na fila`, `Enviando`, `Enviada` e `Falhou`.

### 🌐 UIX-004 — Falha de comunicação

Pare temporariamente o backend e tente carregar ou enviar dados.

Resultado esperado:

- interface não trava;
- apresenta mensagem compreensível;
- botão volta a ficar disponível;
- nenhuma duplicação ocorre após restaurar o backend.

## 🧾 Matriz resumida de status HTTP

| Operação | Sucesso | Erros principais |
|---|---:|---|
| Autenticar | `200` | `400`, `401` |
| Logout | `204` | `403` |
| Cadastrar cliente | `201` | `400`, `409` |
| Consultar perfil | `200` | `403`, `404` |
| Alterar própria senha | `204` | `400`, `403`, `404` |
| Listar clientes | `200` | `403` |
| Atualizar cliente | `200` | `400`, `403`, `404` |
| Redefinir senha | `204` | `400`, `403`, `404` |
| Adicionar crédito | `201` | `400`, `403`, `404`, `409` |
| Ajustar limite | `200` | `400`, `403`, `404`, `409` |
| Listar contatos | `200` | `403` |
| Cadastrar contato | `201` | `400`, `403`, `409` |
| Atualizar contato | `200` | `400`, `403`, `404` |
| Listar conversas | `200` | `403` |
| Criar conversa | `201` | `400`, `403`, `404` |
| Listar mensagens | `200` | `403`, `404` |
| Enviar mensagem | `201` | `400`, `402`, `403`, `404`, `422` |
| Simular recebimento | `201` | `400`, `403`, `404` |
| Marcar como lida | `200` | `403`, `404` |
| Histórico financeiro | `200` | `403`, `404` |

## 🐞 Pontos de atenção e possíveis defeitos

Os itens abaixo devem ser registrados como defeito se forem confirmados durante a execução:

- ⚠️ A área administrativa não possui interface no frontend.
- ⚠️ O cadastro de clientes e contatos não está disponível no frontend.
- ⚠️ A resposta de autenticação utiliza o campo `name`, mas o modelo do frontend espera `clientName`.
- ⚠️ O contador de mensagens não lidas pode permanecer visível até a lista de conversas ser recarregada.
- ⚠️ Reduzir o limite pós-pago para menos que o consumo atual pode resultar em `500 Internal Server Error`; o retorno adequado seria `400` ou `422`.
- ⚠️ UUID inválido em parâmetro de rota pode resultar em `500 Internal Server Error`; o retorno adequado seria `400`.
- ⚠️ Não existe operação pública para simular falha do provedor. O estado `FAILED` só pode ser conferido nos dados iniciais do cliente CNPJ.
- ⚠️ O frontend permite autenticação administrativa, mas não possui uma rota ou experiência específica para o administrador.

## ✅ Critérios de aprovação

A execução pode ser considerada aprovada quando:

- todos os cenários críticos de autenticação, cobrança e mensageria forem aprovados;
- clientes não conseguirem acessar dados de outros clientes;
- operações administrativas rejeitarem usuários sem perfil adequado;
- mensagens normais custarem R$ 0,25 e urgentes R$ 0,50;
- mensagens urgentes forem processadas antes das normais;
- a ordem FIFO for mantida dentro da mesma prioridade;
- falhas financeiras não gerarem mensagem nem transação parcial;
- dados em fila sobreviverem à reinicialização do backend;
- frontend apresentar estados de sucesso e erro compreensíveis;
- não existirem erros inesperados `500` nos fluxos válidos;
- defeitos encontrados estiverem registrados com evidência e severidade.

