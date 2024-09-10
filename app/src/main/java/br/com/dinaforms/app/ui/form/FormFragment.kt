package br.com.dinaforms.app.ui.form

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
        inflater: LayoutInflater, // O inflater para inflar o layout do fragmento.
        container: ViewGroup?,    // O contêiner pai onde o fragmento será adicionado.
        savedInstanceState: Bundle? // O estado salvo do fragmento, se houver.
    ): View {
        // Inicializa o binding para o fragmento usando o layout inflado.
        _binding = FragmentFormBinding.inflate(inflater, container, false)

        // Carrega o JSON do arquivo 'all-fields.json' localizado na pasta assets.
        val jsonData = viewModel.loadJSONFromAsset(requireContext(), "all-fields.json")

        // Converte o JSON carregado em um objeto FormData.
        val formData = viewModel.parseFormData(jsonData)

        // Se o FormData não for nulo, renderiza o formulário dinamicamente.
        formData?.let {
            renderForm(it)
        }

        // Retorna a raiz do layout do fragmento.
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView() // Chama o método da superclasse para garantir a limpeza adequada do ciclo de vida do fragmento.

        // Limpa a referência ao binding para evitar vazamentos de memória.
        _binding = null
    }

    /**
     * Renderiza o formulário dinamicamente com base nos dados fornecidos.
     *
     * @param formData Dados do formulário que incluem seções e campos a serem renderizados.
     */
    private fun renderForm(formData: FormData) {
        // Obtém a lista de seções e campos do formulário.
        val sections = formData.sections
        val fields = formData.fields

        // Organiza os campos por seções, criando um mapa que relaciona o índice da seção com seus campos correspondentes.
        val sectionFields = mutableMapOf<Int, List<Field>>()
        fields.forEach { field ->
            // Encontra a seção à qual o campo pertence com base nos índices.
            val section = sections.find { it.from <= fields.indexOf(field) && it.to >= fields.indexOf(field) }
            section?.let {
                // Adiciona o campo à lista de campos da seção correspondente.
                val fieldList = sectionFields.getOrDefault(it.index, mutableListOf())
                sectionFields[it.index] = fieldList + field
            }
        }

        // Itera sobre cada seção e renderiza seus campos.
        sections.forEach { section ->
            // Cria e adiciona o cabeçalho da seção ao layout.
            createSectionHeader(section.title)

            // Renderiza cada campo dentro da seção atual.
            sectionFields[section.index]?.forEach { field ->
                // Cria o campo apropriado com base no tipo especificado.
                when (field.type) {
                    "text" -> createTextField(field.label, field.required)
                    "email" -> createEmailField(field.label, field.required)
                    "password" -> createPasswordField(field.label, field.required)
                    "number" -> createNumberField(field.label, field.required)
                    "date" -> createDateField(field.label, field.required)
                    "radio" -> createRadioField(field.label, field.options, field.required)
                    "checkbox" -> {
                        // Obtém as opções para o campo checkbox, garantindo que não seja nulo.
                        val options = field.options ?: emptyList()
                        createCheckboxField(field.label, options, field.required)
                    }
                    "dropdown" -> {
                        // Obtém as opções para o campo dropdown, convertendo para uma lista de rótulos.
                        val options = field.options?.map { it.label } ?: emptyList()
                        createDropdownField(field.label, options, field.required)
                    }
                    "description" -> createDescriptionField(field.label)
                    else -> createTextField(field.label, field.required) // Padrão para tipos desconhecidos.
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
        // Cria um TextInputLayout que serve como contêiner para o TextInputEditText.
        val textView = TextInputLayout(requireContext())
        // Define o texto prefixo do TextInputLayout, que aparece ao lado do campo.
        textView.prefixText = label

        // Cria um TextInputEditText para o campo de entrada de texto.
        val editText = TextInputEditText(requireContext())
        // Define o texto de dica que aparece quando o campo está vazio.
        editText.hint = label
        // Define o tipo de entrada como texto simples.
        editText.inputType = InputType.TYPE_CLASS_TEXT

        // Se o campo for obrigatório, define uma mensagem de erro que será exibida quando o campo estiver vazio.
        if (required) {
            editText.error = "$label is required"
        }

        // Adiciona o TextInputLayout e o TextInputEditText ao layout do formulário.
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
        // Cria um TextInputLayout que serve como contêiner para o TextInputEditText.
        val textView = TextInputLayout(requireContext())
        // Define o texto prefixo do TextInputLayout, que aparece ao lado do campo.
        textView.prefixText = label

        // Cria um TextInputEditText para o campo de entrada de texto.
        val editText = TextInputEditText(requireContext())
        // Define o texto de dica que aparece quando o campo está vazio.
        editText.hint = label
        // Define o tipo de entrada como endereço de email, que ativa a validação básica de email.
        editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        // Se o campo for obrigatório, define uma mensagem de erro que será exibida quando o campo estiver vazio.
        if (required) {
            editText.error = "$label is required"
        }

        // Adiciona o TextInputLayout e o TextInputEditText ao layout do formulário.
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
        // Cria um TextInputLayout que serve como contêiner para o TextInputEditText.
        val textView = TextInputLayout(requireContext())
        // Define o texto prefixo do TextInputLayout, que aparece ao lado do campo.
        textView.prefixText = label

        // Cria um TextInputEditText para o campo de entrada de senha.
        val editText = TextInputEditText(requireContext())
        // Define o texto de dica que aparece quando o campo está vazio.
        editText.hint = label
        // Define o tipo de entrada como senha para ocultar o texto digitado.
        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        // Define o método de transformação para ocultar os caracteres da senha.
        editText.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()

        // Se o campo for obrigatório, define uma mensagem de erro que será exibida quando o campo estiver vazio.
        if (required) {
            editText.error = "$label is required"
        }

        // Adiciona o TextInputLayout e o TextInputEditText ao layout do formulário.
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
        // Cria um TextInputLayout que serve como contêiner para o TextInputEditText.
        val textView = TextInputLayout(requireContext())
        // Define o texto prefixo do TextInputLayout, que aparece ao lado do campo.
        textView.prefixText = label

        // Cria um TextInputEditText para o campo de entrada de número.
        val editText = TextInputEditText(requireContext())
        // Define o texto de dica que aparece quando o campo está vazio.
        editText.hint = label
        // Define o tipo de entrada como número para garantir que apenas números sejam digitados.
        editText.inputType = InputType.TYPE_CLASS_NUMBER

        // Se o campo for obrigatório, define uma mensagem de erro que será exibida quando o campo estiver vazio.
        if (required) {
            editText.error = "$label is required"
        }

        // Adiciona o TextInputLayout e o TextInputEditText ao layout do formulário.
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
        // Cria um TextInputLayout que serve como contêiner para o TextInputEditText.
        val textView = TextInputLayout(requireContext())
        // Define o texto prefixo do TextInputLayout, que aparece ao lado do campo.
        textView.prefixText = label

        // Cria um TextInputEditText para o campo de entrada de data.
        val editText = TextInputEditText(requireContext())
        // Define o texto de dica que aparece quando o campo está vazio.
        editText.hint = label
        // Define o tipo de entrada como data e hora.
        editText.inputType = InputType.TYPE_CLASS_DATETIME

        // Define um listener para o clique no TextInputEditText para abrir um DatePickerDialog.
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Cria um DatePickerDialog para selecionar a data.
            val datePicker = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Define a data selecionada no formato DD/MM/YYYY no campo de texto.
                    editText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                }, year, month, day)

            // Mostra o DatePickerDialog.
            datePicker.show()
        }

        // Se o campo for obrigatório, define uma mensagem de erro que será exibida quando o campo estiver vazio.
        if (required) {
            editText.error = "$label is required"
        }

        // Adiciona o TextInputLayout e o TextInputEditText ao layout do formulário.
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
        // Cria um MaterialTextView para exibir o rótulo do campo de seleção.
        val textView = MaterialTextView(requireContext()).apply {
            text = label
        }

        // Cria um RadioGroup que conterá os RadioButtons.
        val radioGroup = RadioGroup(requireContext()).apply {
            // Adiciona RadioButtons ao RadioGroup com base nas opções fornecidas.
            options?.forEach { option ->
                val radioButton = MaterialRadioButton(requireContext()).apply {
                    text = option.label
                }
                addView(radioButton)
            }
        }

        // Cria um MaterialTextView para exibir mensagens de erro, inicialmente invisível.
        val errorTextView = MaterialTextView(requireContext()).apply {
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            visibility = View.GONE
        }

        // Se o campo for obrigatório, adiciona um listener para o RadioGroup.
        // O listener oculta a mensagem de erro quando uma opção é selecionada.
        if (required) {
            radioGroup.setOnCheckedChangeListener { _, _ ->
                errorTextView.visibility = View.GONE
            }
        }

        // Adiciona o rótulo, o RadioGroup e a mensagem de erro ao layout do formulário.
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
        // Cria um LinearLayout vertical para agrupar os CheckBoxes.
        val checkboxLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20) // Define o preenchimento interno do LinearLayout.
        }

        // Cria um MaterialTextView para exibir o rótulo do campo de seleção.
        val headerTextView = MaterialTextView(requireContext()).apply {
            text = label
            textSize = 18f // Define o tamanho do texto.
            setPadding(0, 0, 0, 10) // Define o preenchimento abaixo do TextView.
        }
        checkboxLayout.addView(headerTextView) // Adiciona o TextView ao LinearLayout.

        // Itera sobre as opções e cria um CheckBox para cada uma.
        options.forEach { option ->
            val checkBox = MaterialCheckBox(requireContext()).apply {
                text = option.label
                tag = option.value // Usa o valor da opção como a tag do CheckBox.
                setPadding(0, 10, 0, 10) // Define o preenchimento vertical para cada CheckBox.
            }
            checkboxLayout.addView(checkBox) // Adiciona o CheckBox ao LinearLayout.
        }

        // Se o campo for obrigatório, cria uma mensagem de erro que será exibida se o campo não for preenchido.
        if (required) {
            val errorTextView = MaterialTextView(requireContext()).apply {
                text = "$label is required" // Define a mensagem de erro.
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                visibility = View.GONE // Inicialmente oculta a mensagem de erro.
            }
            checkboxLayout.addView(errorTextView) // Adiciona a mensagem de erro ao LinearLayout.
        }

        // Adiciona o LinearLayout contendo todos os CheckBoxes e o rótulo ao layout do formulário.
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
        // Cria um MaterialTextView para exibir o rótulo do campo de seleção.
        val textView = MaterialTextView(requireContext()).apply {
            text = label // Define o texto do TextView como o rótulo do campo.
        }

        // Cria um Spinner (dropdown) para a seleção de opções.
        val spinner = Spinner(requireContext()).apply {
            // Cria um ArrayAdapter para fornecer as opções ao Spinner.
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options).apply {
                // Define o layout do item dropdown.
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            this.adapter = adapter // Define o adapter do Spinner.

            // Define o texto de prompt, exibido quando nenhuma opção está selecionada.
            if (required) {
                prompt = "$label is required" // Configura o prompt para indicar que o campo é obrigatório.
            }
        }

        // Adiciona o MaterialTextView e o Spinner ao layout do formulário.
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
        // Cria um LinearLayout vertical para conter o campo de descrição e os botões de formatação.
        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL // Define a orientação vertical.
            background = ContextCompat.getDrawable(requireContext(), R.drawable.border_edittext) // Define o fundo com uma borda.
            setPadding(0, 20, 0, 0) // Define o padding do layout.
        }

        // Cria um LinearLayout horizontal para os botões de formatação.
        val buttonLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL // Define a orientação horizontal.
        }

        // Cria um TextInputEditText para a entrada de texto com suporte a HTML.
        val editText = TextInputEditText(requireContext()).apply {
            setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT)) // Define o conteúdo HTML.
            gravity = Gravity.TOP // Alinha o texto no topo.
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE // Permite múltiplas linhas.
            isSingleLine = false // Desativa a única linha.
            setPadding(10, -10, 10, 20) // Define o padding interno.
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                height = LinearLayout.LayoutParams.WRAP_CONTENT // Ajusta a altura do layout.
            }
            minHeight = 300 // Define a altura mínima.
        }

        // Cria um botão para aplicar negrito ao texto selecionado.
        val boldButton = Button(requireContext()).apply {
            text = "B" // Define o texto do botão como "B".
            setOnClickListener {
                applyStyleToSelection(editText, Typeface.BOLD) // Aplica o estilo negrito ao texto selecionado.
            }
        }

        // Cria um botão para aplicar itálico ao texto selecionado.
        val italicButton = Button(requireContext()).apply {
            text = "I" // Define o texto do botão como "I".
            setOnClickListener {
                applyStyleToSelection(editText, Typeface.ITALIC) // Aplica o estilo itálico ao texto selecionado.
            }
        }

        // Cria um botão para aplicar sublinhado ao texto selecionado.
        val underlineButton = Button(requireContext()).apply {
            text = "U" // Define o texto do botão como "U".
            setOnClickListener {
                applyUnderlineToSelection(editText) // Aplica o sublinhado ao texto selecionado.
            }
        }

        // Cria um botão para inserir um link no texto.
        val linkButton = Button(requireContext()).apply {
            text = "Link" // Define o texto do botão como "Link".
            setOnClickListener {
                insertLink(editText) // Insere um link no texto.
            }
        }

        // Adiciona os botões de formatação ao LinearLayout horizontal.
        buttonLayout.addView(boldButton)
        buttonLayout.addView(italicButton)
        buttonLayout.addView(underlineButton)
        buttonLayout.addView(linkButton)

        // Adiciona o LinearLayout dos botões e o campo de descrição ao LinearLayout principal.
        linearLayout.addView(buttonLayout)
        linearLayout.addView(editText)

        // Adiciona o LinearLayout ao layout do formulário.
        binding.formLayout.addView(linearLayout)
    }


    /**
     * Aplica um estilo ao texto selecionado no EditText.
     *
     * @param editText O EditText no qual o estilo será aplicado.
     * @param style O estilo a ser aplicado, como Typeface.BOLD ou Typeface.ITALIC.
     */
    private fun applyStyleToSelection(editText: EditText, style: Int) {
        val start = editText.selectionStart // Obtém o início da seleção.
        val end = editText.selectionEnd // Obtém o fim da seleção.
        val spannable = editText.text as Spannable // Obtém o texto do EditText como um Spannable para aplicar spans.

        // Aplica o estilo ao texto selecionado.
        spannable.setSpan(android.text.style.StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    /**
     * Aplica sublinhado ao texto selecionado no EditText.
     *
     * @param editText O EditText no qual o sublinhado será aplicado.
     */
    private fun applyUnderlineToSelection(editText: EditText) {
        val start = editText.selectionStart // Obtém o início da seleção.
        val end = editText.selectionEnd // Obtém o fim da seleção.
        val spannable = editText.text as Spannable // Obtém o texto do EditText como um Spannable para aplicar spans.

        // Aplica o sublinhado ao texto selecionado.
        spannable.setSpan(android.text.style.UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    /**
     * Insere um link no texto selecionado no EditText.
     *
     * @param editText O EditText no qual o link será inserido.
     */
    private fun insertLink(editText: EditText) {
        val start = editText.selectionStart // Obtém o início da seleção.
        val end = editText.selectionEnd // Obtém o fim da seleção.
        val selectedText = editText.text.subSequence(start, end).toString() // Obtém o texto selecionado.

        // Verifica se há um texto selecionado para o link.
        if (selectedText.isNotEmpty()) {
            val url = "https://www.exemplo.com" // Define a URL para o link.

            val spannable = editText.text as Spannable // Obtém o texto do EditText como um Spannable para aplicar spans.

            // Aplica o link ao texto selecionado.
            spannable.setSpan(android.text.style.URLSpan(url), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * Cria um cabeçalho de seção a partir de conteúdo HTML.
     *
     * @param htmlContent O conteúdo HTML que define a estrutura do cabeçalho da seção.
     */
    private fun createSectionHeader(htmlContent: String) {
        // Analisa o conteúdo HTML usando Jsoup.
        val document = Jsoup.parse(htmlContent)
        val elements = document.body().children() // Obtém todos os elementos filhos do corpo do HTML.

        // Itera sobre os elementos HTML para criar views correspondentes.
        elements.forEach { element ->
            when (element.tagName()) {
                // Cria um TextView para elementos <h1>.
                "h1" -> {
                    val textView = TextView(requireContext()).apply {
                        text = element.text() // Define o texto do TextView como o texto do elemento <h1>.
                        textSize = 30f // Define o tamanho do texto.
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black)) // Define a cor do texto.
                        gravity = Gravity.START // Define o alinhamento do texto.
                        setTypeface(null, Typeface.BOLD) // Define o estilo do texto como negrito.
                        setPadding(0, 24, 0, 24) // Define o padding ao redor do texto.
                    }
                    // Adiciona o TextView à disposição do formulário.
                    binding.formLayout.addView(textView)
                }

                // Cria um TextView para elementos <p>.
                "p" -> {
                    val textView = TextView(requireContext()).apply {
                        text = element.text() // Define o texto do TextView como o texto do elemento <p>.
                        textSize = 16f // Define o tamanho do texto.
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black)) // Define a cor do texto.
                        setPadding(0, 8, 0, 8) // Define o padding ao redor do texto.
                    }
                    // Adiciona o TextView à disposição do formulário.
                    binding.formLayout.addView(textView)

                    // Verifica se o elemento <p> contém imagens.
                    val img = element.select("img")
                    if (img.isNotEmpty()) {
                        val imgUrl = img.attr("src") // Obtém a URL da imagem.
                        // Cria um ImageView para a imagem e adiciona ao layout.
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
        // Obtém as métricas de display do dispositivo para ajustar a largura da imagem.
        val displayMetrics = resources.displayMetrics
        // Calcula a largura da imagem como 50% da largura da tela.
        val imageWidth = (displayMetrics.widthPixels * 0.5).toInt()

        // Cria um ImageView e configura suas propriedades.
        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                imageWidth, // Define a largura da imagem.
                LinearLayout.LayoutParams.WRAP_CONTENT, // Define a altura da imagem para ajustar ao conteúdo.
            ).apply {
                // Define margens ao redor do ImageView.
                setMargins(0, 16, 0, 20)
                // Define a gravidade para centralizar o ImageView horizontalmente.
                gravity = Gravity.CENTER
            }
            // Define o tipo de escala da imagem para ajustar a imagem dentro do ImageView.
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        // Utiliza o Glide para carregar a imagem a partir da URL e exibi-la no ImageView.
        Glide.with(requireContext())
            .load(imgUrl) // Define a URL da imagem.
            .into(imageView) // Define o ImageView onde a imagem será exibida.

        // Adiciona o ImageView ao layout do formulário.
        binding.formLayout.addView(imageView)
    }
}
