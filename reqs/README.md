# Requisições de usuários para o Insomnia

## Importação

1. Abra o Insomnia.
2. Selecione a opção para importar dados de um arquivo.
3. Escolha `usuarios-insomnia.json`.
4. Abra o ambiente `Ambiente local` da coleção.
5. Confirme que `base_url` está definido como `http://localhost:8080`.

## Preparação

1. Execute `Autenticar administrador`.
2. Copie o valor de `token` retornado.
3. Cole o valor na variável `admin_token` do ambiente, sem adicionar `Bearer`.
4. Execute o cadastro do usuário pré-pago ou pós-pago.
5. Copie o campo `id` retornado para `cliente_pre_pago_id` ou `cliente_pos_pago_id`.
6. Execute as requisições de alteração desejadas.

## Perfis suportados

A API cadastra usuários com o perfil `CLIENT` e oferece dois planos:

- `PREPAID`: cliente pré-pago, com saldo;
- `POSTPAID`: cliente pós-pago, com limite e consumo mensal.

Não existe endpoint para cadastrar um novo usuário com perfil `ADMIN`. O administrador inicial é criado pela carga de dados do banco.

## Credenciais iniciais

| Perfil | Documento | Senha |
|---|---|---|
| Administrador | `11222333000181` | `Admin@123` |
| Cliente pré-pago | `52998224725` | `Client@123` |
| Cliente pós-pago | `11144477735` | `Client@123` |

## Observações

- Os documentos dos exemplos são válidos e precisam ser únicos no banco.
- Se um cadastro retornar `409 Conflict`, altere o documento ou restaure o volume do banco.
- A alteração cadastral permite modificar somente o nome e a situação ativa.
- A alteração de plano ou perfil não é oferecida pela API atual.
- A redefinição administrativa de senha não exige a senha anterior.

