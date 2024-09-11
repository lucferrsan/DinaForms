package br.com.dinaforms.app
/**
 * Autor: Luciano
 * Projeto: DinaForms App
 * Data: 10 de setembro de 2024
 * E-mail: lucferrsan@gmail.com
 *
 * Descrição:
 * A `MainActivity` é a atividade principal do aplicativo DinaForms, responsável por configurar a navegação
 * entre as diferentes telas, utilizando o `BottomNavigationView` e o `NavController`. Esta atividade faz uso
 * do padrão de navegação recomendado pelo Android Jetpack para gerenciar a interface do usuário e permitir
 * uma navegação fluida entre as seções do aplicativo.
 *
 * Funcionalidades principais:
 * - Inicializa a barra de navegação inferior (`BottomNavigationView`) e a associa ao `NavController`, permitindo
 *   que o usuário alterne entre diferentes fragmentos.
 * - Configura a `AppBar` para sincronizar com o `NavController`, atualizando o título da tela com base na
 *   navegação atual.
 * - Utiliza o `ActivityMainBinding` para acessar e inflar a visualização da atividade principal.
 *
 * Componentes:
 * - `binding`: Usado para inflar a visualização e acessar os elementos da interface definidos no arquivo XML.
 * - `navView`: Referência ao `BottomNavigationView`, que gerencia as opções de navegação na parte inferior da tela.
 * - `navController`: Gerencia a navegação entre os fragmentos.
 * - `appBarConfiguration`: Configuração da barra de título para sincronizar com a navegação.
 *
 * Considerações futuras:
 * - Adicionar novas telas e fragmentos à navegação, conforme necessário.
 * - Implementar lógica adicional para alterar o comportamento de navegação com base no estado do usuário ou
 *   preferências.
 */

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.dinaforms.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_form
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}