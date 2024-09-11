package br.com.dinaforms.app.ui.form
/**
 * Autor: Luciano Santos
 * Projeto: DinaForms App
 * Data: 10 de setembro de 2024
 * E-mail: lucferrsan@gmail.com
 *
 * Descrição:
 * Este fragmento (`FormFragment`) faz parte do aplicativo DinaForms, que renderiza dinamicamente
 * formulários com base em um arquivo JSON. Os campos de formulário são criados programaticamente
 * utilizando componentes do Material You, e o conteúdo HTML pode ser processado e exibido nas
 * seções. A implementação inclui suporte para os seguintes tipos de campos:
 * - Texto, Email, Senha, Número, Data
 * - Botões de Rádio, Checkboxes, Dropdowns
 * - Áreas de Descrição com conteúdo HTML
 *
 * O JSON lido do arquivo assets contém uma estrutura que define as seções e os campos do formulário,
 * permitindo que o formulário seja totalmente configurável. A lógica principal de renderização está
 * dividida em métodos específicos para cada tipo de campo. Além disso, a funcionalidade de
 * validação de campos obrigatórios está implementada para os campos mais importantes.
 *
 * Funcionalidades:
 * - Renderização dinâmica de formulários com base em JSON
 * - Suporte para múltiplos tipos de campo e validação
 * - Integração com Material Design para consistência de UI
 * - Suporte à exibição de conteúdo HTML nas descrições
 * - Implementação de componentes de UI como TextInputLayout, MaterialRadioButton, MaterialCheckBox
 *
 * O fragmento utiliza um ViewModel (`FormViewModel`) para carregar e processar o JSON, e as ações
 * do usuário nos campos serão manipuladas para armazenar dados localmente via SQLite.
 *
 * Atualizações futuras:
 * - Implementar auto-salvamento de valores preenchidos nos campos de formulário
 * - Melhorar o desempenho para formulários grandes
 * - Suporte para carregamento de imagens no conteúdo HTML das seções
 */

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.Spannable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.dinaforms.app.R
import br.com.dinaforms.app.databinding.FragmentFormBinding
import com.bumptech.glide.Glide
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import org.jsoup.Jsoup
import java.util.Calendar

class FormFragment : Fragment() {
    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel = FormViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)

        val jsonData = viewModel.loadJSONFromAsset(requireContext(), "all-fields.json")

        val formData = viewModel.parseFormData(jsonData)

        formData?.let {
            renderForm(it)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    /**
     * Renderiza o formulário dinamicamente com base nos dados fornecidos.
     *
     * @param formData Dados do formulário que incluem seções e campos a serem renderizados.
     */
    private fun renderForm(formData: FormData) {
        val sections = formData.sections
        val fields = formData.fields

        val sectionFields = mutableMapOf<Int, List<Field>>()
        fields.forEach { field ->
            val section = sections.find { it.from <= fields.indexOf(field) && it.to >= fields.indexOf(field) }
            section?.let {
                val fieldList = sectionFields.getOrDefault(it.index, mutableListOf())
                sectionFields[it.index] = fieldList + field
            }
        }

        sections.forEach { section ->
            createSectionHeader(section.title)

            sectionFields[section.index]?.forEach { field ->
                when (field.type) {
                    "text" -> createTextField(field.label, field.required)
                    "email" -> createEmailField(field.label, field.required)
                    "password" -> createPasswordField(field.label, field.required)
                    "number" -> createNumberField(field.label, field.required)
                    "date" -> createDateField(field.label, field.required)
                    "radio" -> createRadioField(field.label, field.options, field.required)
                    "checkbox" -> {
                        val options = field.options ?: emptyList()
                        createCheckboxField(field.label, options, field.required)
                    }
                    "dropdown" -> {
                        val options = field.options?.map { it.label } ?: emptyList()
                        createDropdownField(field.label, options, field.required)
                    }
                    "description" -> createDescriptionField(field.label)
                    else -> createTextField(field.label, field.required)
                }
            }
        }
    }

    /**
     * Cria um campo de texto (TextInputLayout e TextInputEditText) e o adiciona ao layout do formulário.
     *
     * @param label Rótulo do campo de texto que será exibido ao usuário.
     * @param required Indica se o campo é obrigatório ou não.
     */
    private fun createTextField(label: String, required: Boolean) {
        val textView = TextInputLayout(requireContext())
        textView.prefixText = label

        val editText = TextInputEditText(requireContext())
        editText.hint = label
        editText.inputType = InputType.TYPE_CLASS_TEXT

        if (required) {
            editText.error = "$label is required"
        }

        binding.formLayout.addView(textView)
        binding.formLayout.addView(editText)
    }

    /**
     * Cria um campo de email (TextInputLayout e TextInputEditText) e o adiciona ao layout do formulário.
     *
     * @param label Rótulo do campo de email que será exibido ao usuário.
     * @param required Indica se o campo é obrigatório ou não.
     */
    private fun createEmailField(label: String, required: Boolean) {
        val textView = TextInputLayout(requireContext())
        textView.prefixText = label

        val editText = TextInputEditText(requireContext())
        editText.hint = label
        editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        if (required) {
            editText.error = "$label is required"
        }

        binding.formLayout.addView(textView)
        binding.formLayout.addView(editText)
    }

    /**
     * Cria um campo de senha (TextInputLayout e TextInputEditText) e o adiciona ao layout do formulário.
     *
     * @param label Rótulo do campo de senha que será exibido ao usuário.
     * @param required Indica se o campo é obrigatório ou não.
     */
    private fun createPasswordField(label: String, required: Boolean) {
        val textView = TextInputLayout(requireContext())
        textView.prefixText = label

        val editText = TextInputEditText(requireContext())
        editText.hint = label
        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        editText.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()

        if (required) {
            editText.error = "$label is required"
        }

        binding.formLayout.addView(textView)
        binding.formLayout.addView(editText)
    }

    /**
     * Cria um campo de número (TextInputLayout e TextInputEditText) e o adiciona ao layout do formulário.
     *
     * @param label Rótulo do campo de número que será exibido ao usuário.
     * @param required Indica se o campo é obrigatório ou não.
     */
    private fun createNumberField(label: String, required: Boolean) {
        val textView = TextInputLayout(requireContext())
        textView.prefixText = label

        val editText = TextInputEditText(requireContext())
        editText.hint = label
        editText.inputType = InputType.TYPE_CLASS_NUMBER

        if (required) {
            editText.error = "$label is required"
        }

        binding.formLayout.addView(textView)
        binding.formLayout.addView(editText)
    }

    /**
     * Cria um campo de data (TextInputLayout e TextInputEditText) e o adiciona ao layout do formulário.
     * O campo exibe um seletor de data quando clicado.
     *
     * @param label Rótulo do campo de data que será exibido ao usuário.
     * @param required Indica se o campo é obrigatório ou não.
     */
    @SuppressLint("SetTextI18n")
    private fun createDateField(label: String, required: Boolean) {
        val textView = TextInputLayout(requireContext())
        textView.prefixText = label

        val editText = TextInputEditText(requireContext())
        editText.hint = label
        editText.inputType = InputType.TYPE_CLASS_DATETIME

        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    editText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                }, year, month, day)

            datePicker.show()
        }

        if (required) {
            editText.error = "$label is required"
        }

        binding.formLayout.addView(textView)
        binding.formLayout.addView(editText)
    }

    /**
     * Cria um campo de seleção por rádio (RadioGroup com RadioButtons) e o adiciona ao layout do formulário.
     *
     * @param label Rótulo do campo de seleção que será exibido ao usuário.
     * @param options Lista de opções para o RadioGroup, onde cada opção é um RadioButton.
     * @param required Indica se o campo é obrigatório ou não.
     */
    private fun createRadioField(label: String, options: List<Option>?, required: Boolean) {
        val textView = MaterialTextView(requireContext()).apply {
            text = label
        }

        val radioGroup = RadioGroup(requireContext()).apply {
            options?.forEach { option ->
                val radioButton = MaterialRadioButton(requireContext()).apply {
                    text = option.label
                }
                addView(radioButton)
            }
        }

        val errorTextView = MaterialTextView(requireContext()).apply {
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            visibility = View.GONE
        }

        if (required) {
            radioGroup.setOnCheckedChangeListener { _, _ ->
                errorTextView.visibility = View.GONE
            }
        }

        binding.formLayout.addView(textView)
        binding.formLayout.addView(radioGroup)
        binding.formLayout.addView(errorTextView)
    }

    /**
     * Cria um campo de seleção por checkbox (LinearLayout com CheckBoxes) e o adiciona ao layout do formulário.
     *
     * @param label Rótulo do campo de seleção que será exibido ao usuário.
     * @param options Lista de opções para os CheckBoxes, onde cada opção é um CheckBox.
     * @param required Indica se o campo é obrigatório ou não.
     */
    @SuppressLint("SetTextI18n")
    private fun createCheckboxField(label: String, options: List<Option>, required: Boolean) {
        val checkboxLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }

        val headerTextView = MaterialTextView(requireContext()).apply {
            text = label
            textSize = 18f
            setPadding(0, 0, 0, 10)
        }
        checkboxLayout.addView(headerTextView)

        options.forEach { option ->
            val checkBox = MaterialCheckBox(requireContext()).apply {
                text = option.label
                tag = option.value
                setPadding(0, 10, 0, 10)
            }
            checkboxLayout.addView(checkBox)
        }

        if (required) {
            val errorTextView = MaterialTextView(requireContext()).apply {
                text = "$label is required"
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                visibility = View.GONE
            }
            checkboxLayout.addView(errorTextView)
        }

        binding.formLayout.addView(checkboxLayout)
    }



    /**
     * Cria um campo de seleção por dropdown (Spinner) e o adiciona ao layout do formulário.
     *
     * @param label Rótulo do campo de seleção que será exibido ao usuário.
     * @param options Lista de opções para o dropdown.
     * @param required Indica se o campo é obrigatório ou não.
     */
    private fun createDropdownField(label: String, options: List<String>, required: Boolean) {
        val textView = MaterialTextView(requireContext()).apply {
            text = label
        }

        val spinner = Spinner(requireContext()).apply {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            this.adapter = adapter

            if (required) {
                prompt = "$label is required"
            }
        }

        binding.formLayout.addView(textView)
        binding.formLayout.addView(spinner)
    }


    /**
     * Cria um campo de descrição com suporte a formatação HTML e o adiciona ao layout do formulário.
     *
     * @param htmlContent Conteúdo HTML que será exibido no campo de descrição.
     */
    @SuppressLint("SetTextI18n")
    private fun createDescriptionField(htmlContent: String) {
        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(requireContext(), R.drawable.border_edittext)
            setPadding(0, 20, 0, 0)
        }

        val buttonLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val editText = TextInputEditText(requireContext()).apply {
            setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT))
            gravity = Gravity.TOP
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            isSingleLine = false
            setPadding(10, -10, 10, 20)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                height = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            minHeight = 300
        }

        val boldButton = Button(requireContext()).apply {
            text = "B"
            setOnClickListener {
                applyStyleToSelection(editText, Typeface.BOLD)
            }
        }

        val italicButton = Button(requireContext()).apply {
            text = "I"
            setOnClickListener {
                applyStyleToSelection(editText, Typeface.ITALIC)
            }
        }

        val underlineButton = Button(requireContext()).apply {
            text = "U"
            setOnClickListener {
                applyUnderlineToSelection(editText)
            }
        }

        val linkButton = Button(requireContext()).apply {
            text = "Link"
            setOnClickListener {
                insertLink(editText)
            }
        }

        buttonLayout.addView(boldButton)
        buttonLayout.addView(italicButton)
        buttonLayout.addView(underlineButton)
        buttonLayout.addView(linkButton)

        linearLayout.addView(buttonLayout)
        linearLayout.addView(editText)

        binding.formLayout.addView(linearLayout)
    }


    /**
     * Aplica um estilo ao texto selecionado no EditText.
     *
     * @param editText O EditText no qual o estilo será aplicado.
     * @param style O estilo a ser aplicado, como Typeface.BOLD ou Typeface.ITALIC.
     */
    private fun applyStyleToSelection(editText: EditText, style: Int) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val spannable = editText.text as Spannable

        spannable.setSpan(android.text.style.StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    /**
     * Aplica sublinhado ao texto selecionado no EditText.
     *
     * @param editText O EditText no qual o sublinhado será aplicado.
     */
    private fun applyUnderlineToSelection(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val spannable = editText.text as Spannable

        spannable.setSpan(android.text.style.UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    /**
     * Insere um link no texto selecionado no EditText.
     *
     * @param editText O EditText no qual o link será inserido.
     */
    private fun insertLink(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val selectedText = editText.text.subSequence(start, end).toString()

        if (selectedText.isNotEmpty()) {
            val url = "https://www.exemplo.com"
            val spannable = editText.text as Spannable

            spannable.setSpan(android.text.style.URLSpan(url), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * Cria um cabeçalho de seção a partir de conteúdo HTML.
     *
     * @param htmlContent O conteúdo HTML que define a estrutura do cabeçalho da seção.
     */
    private fun createSectionHeader(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val elements = document.body().children()

        elements.forEach { element ->
            when (element.tagName()) {
                "h1" -> {
                    val textView = TextView(requireContext()).apply {
                        text = element.text()
                        textSize = 30f
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                        gravity = Gravity.START
                        setTypeface(null, Typeface.BOLD)
                        setPadding(0, 24, 0, 24)
                    }
                    binding.formLayout.addView(textView)
                }

                "p" -> {
                    val textView = TextView(requireContext()).apply {
                        text = element.text()
                        textSize = 16f
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                        setPadding(0, 8, 0, 8)
                    }
                    binding.formLayout.addView(textView)

                    val img = element.select("img")
                    if (img.isNotEmpty()) {
                        val imgUrl = img.attr("src")
                        createImageView(imgUrl)
                    }
                }
            }
        }
    }

    /**
     * Cria e adiciona um ImageView ao layout, carregando uma imagem a partir de uma URL.
     *
     * @param imgUrl URL da imagem que será carregada e exibida no ImageView.
     */
    private fun createImageView(imgUrl: String) {
        val displayMetrics = resources.displayMetrics
        val imageWidth = (displayMetrics.widthPixels * 0.5).toInt()

        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                imageWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                setMargins(0, 16, 0, 20)
                gravity = Gravity.CENTER
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        Glide.with(requireContext())
            .load(imgUrl)
            .into(imageView)

        binding.formLayout.addView(imageView)
    }
}
