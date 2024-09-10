package br.com.dinaforms.app.ui.form

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

