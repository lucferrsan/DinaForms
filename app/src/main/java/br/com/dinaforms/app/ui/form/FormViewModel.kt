package br.com.dinaforms.app.ui.form
/**
 * Autor: Luciano
 * Projeto: DinaForms App
 * Data: 10 de setembro de 2024
 * E-mail: lucferrsan@gmail.com
 *
 * Descrição:
 * Esta classe (`FormViewModel`) faz parte da arquitetura MVVM do aplicativo DinaForms.
 * Ela é responsável pelo carregamento e processamento de arquivos JSON que definem a estrutura
 * dos formulários dinâmicos a serem exibidos na interface do usuário.
 *
 * Funções principais:
 * - `loadJSONFromAsset`: Carrega um arquivo JSON localizado na pasta assets do aplicativo,
 *   convertendo-o em uma string UTF-8. Esta função lida com possíveis exceções de I/O para
 *   garantir que o aplicativo não trave em caso de falha de leitura.
 * - `parseFormData`: Converte a string JSON carregada em um objeto `FormData`, que contém a
 *   estrutura dos campos do formulário a serem renderizados. Utiliza a biblioteca Gson para
 *   desserializar o JSON.
 *
 * Responsabilidades:
 * - Gerenciar o carregamento dos dados JSON que configuram os formulários.
 * - Converter os dados JSON em um formato utilizável para a renderização dos componentes de UI.
 *
 * Atualizações futuras:
 * - Implementar o salvamento automático de dados do formulário em uma tabela SQLite
 * - Melhorar o desempenho da função de parsing para grandes volumes de dados
 */

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.IOException

class FormViewModel() : ViewModel() {
    fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun parseFormData(jsonData: String?): FormData? {
        return jsonData?.let {
            Gson().fromJson(it, FormData::class.java)
        }
    }
}
