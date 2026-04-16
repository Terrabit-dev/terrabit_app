# Package di

## Sobre este Package

En este package se hace uso del Hilt (Biblioteca de insercion de dependencias de Android), permite reducir el trabajo
repetitivo de insertar dependencias de forma manual.


## /terrabit_app/di/AppModule.kt

Ensenya a Hilt como construir UserPreferences. 
@Singleton -> Se crea una sola instancia en toda la app
@ApplicationContext -> Inyecta el contexto de manera automatica


## /terrabit_app/di/NetworkModule.kt

Es la seccion de network de la app, todo lo que tiene que ver con los endpoints
el endpoint base esta aqui y las dependencias del network lo llaman aqui.