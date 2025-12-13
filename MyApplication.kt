package com.example.gestaoderisco

import android.app.Application

class MyApplication : Application() {
    // Este é o local ideal para inicializar bibliotecas que precisam do contexto do aplicativo,
    // como Hilt (para injeção de dependência), Timber (para logs), etc.
}
