# DinaForms

DinaForms é um aplicativo Android para criação e gerenciamento de formulários dinâmicos. Desenvolvido em Kotlin, o aplicativo carrega e processa formulários a partir de arquivos JSON, permitindo a renderização e manipulação de campos de formulário de forma dinâmica.

## Funcionalidades

- **Carregamento Dinâmico de Formulários**: Carrega formulários a partir de arquivos JSON localizados na pasta `assets`.
- **Renderização de Campos**: Cria e exibe campos de formulário dinâmicos, incluindo:
  - Campos de texto, e-mail, senha e número
  - Seletores de data
  - Botões de rádio e caixas de seleção
  - Menus suspensos (dropdowns)
  - Áreas de descrição com suporte a HTML
- **Exibição de Imagens**: Renderiza imagens a partir de URLs fornecidas no conteúdo HTML das seções.

## Arquivos JSON

- **`200-form.json`**: Define a estrutura e os campos do formulário. Este arquivo é utilizado para criar e configurar os campos dinâmicos no formulário.
- **`all-fields.json`**: Contém todas as definições possíveis de campos e opções para os formulários, incluindo tipos de campo, rótulos e opções.

## Estrutura do Projeto

### Classes de Dados

- **`FormData`**: Representa a estrutura geral do formulário, incluindo campos e seções.
- **`Field`**: Representa um campo individual no formulário, com tipo, rótulo, nome, e opções se aplicável.
- **`Option`**: Representa opções para campos de dropdown e rádio.
- **`Section`**: Representa uma seção do formulário, com título e intervalos de campos.

### ViewModel

- **`FormViewModel`**: Carrega o JSON dos formulários a partir dos assets e o converte em um objeto `FormData`. Fornece métodos para carregar e processar o JSON.

### Funcionalidades Implementadas

- **Campos de Texto**: Campos para texto, e-mail, senha e número.
- **Campos de Data**: Seletores de data usando um `DatePicker`.
- **Campos de Rádio e Checkbox**: Opções de rádio e caixas de seleção.
- **Dropdowns**: Menus suspensos para seleção de opções.
- **Descrição com HTML**: Renderização de HTML em campos de descrição, incluindo suporte para formatação de texto e links.

### Funcionalidades a Serem Implementadas

1. **Integração com Banco de Dados**:
   - A funcionalidade de armazenamento dos dados dos formulários em um banco de dados SQLite ainda não foi implementada.

2. **Auto-Save**:
   - A funcionalidade de auto-save para os campos do formulário, garantindo que os dados sejam salvos automaticamente, ainda precisa ser desenvolvida.

3. **Adição Dinâmica de Novos Formulários**:
   - A capacidade de adicionar novos formulários dinamicamente à interface do usuário em tempo real ainda não está implementada.

## Como Contribuir

1. **Clone o Repositório**:
   ```bash
   git clone https://github.com/seu-usuario/dinaforms.git
