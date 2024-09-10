package br.com.dinaforms.app.ui.form

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
