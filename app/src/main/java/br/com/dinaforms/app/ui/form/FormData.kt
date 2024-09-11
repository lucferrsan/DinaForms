package br.com.dinaforms.app.ui.form
/**
 * Autor: Luciano Santos
 * Projeto: DinaForms App
 * Data: 10 de setembro de 2024
 * E-mail: lucferrsan@gmail.com
 *
 * Descrição:
 * Estas classes de dados representam a estrutura do JSON utilizado no aplicativo DinaForms
 * para a criação de formulários dinâmicos. Elas mapeiam diretamente os dados do arquivo JSON
 * e permitem a fácil manipulação e renderização dos campos e seções do formulário.
 *
 * Classes principais:
 * - `FormData`: Representa a estrutura de um formulário, contendo um título (`title`),
 *   uma lista de campos (`fields`) e uma lista de seções (`sections`).
 * - `Field`: Define os atributos de cada campo individual no formulário, como tipo (`type`),
 *   rótulo (`label`), nome (`name`), obrigatoriedade (`required`), UUID (`uuid`), e opções
 *   (`options`) para campos do tipo dropdown e radio.
 * - `Option`: Representa as opções de um campo de seleção, contendo um rótulo (`label`) e
 *   um valor (`value`), usado para dropdown e radio buttons.
 * - `Section`: Define a estrutura de uma seção dentro do formulário, com um título (`title`),
 *   o intervalo de campos que pertencem à seção (de `from` até `to`), índice da seção (`index`)
 *   e um UUID exclusivo (`uuid`).
 *
 * Responsabilidades:
 * - Facilitar o mapeamento entre o JSON e os componentes de UI.
 * - Fornecer uma estrutura clara para a organização dos campos e seções dentro do formulário.
 *
 * Atualizações futuras:
 * - Suporte para novos tipos de campos além dos já implementados.
 * - Inclusão de validações adicionais para campos obrigatórios.
 */

// Classes de dados para mapear o JSON
data class FormData(
    val title: String,
    val fields: List<Field>,
    val sections: List<Section>
)

data class Field(
    val type: String,
    val label: String,
    val name: String,
    val required: Boolean,
    val uuid: String,
    val options: List<Option>? = null // para dropdown e radio
)

data class Option(
    val label: String,
    val value: String
)

data class Section(
    val title: String,
    val from: Int,
    val to: Int,
    val index: Int,
    val uuid: String
)

