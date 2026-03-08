# Sistema-Gestor-de-Ventas-para-Ferreter-a
Sistema Gestor de Ventas para Ferretería

Sistema de gestión de ventas desarrollado en Java con arquitectura cliente-servidor, orientado al control y administración de productos, clientes, usuarios, pedidos y ventas dentro de una ferretería.

El sistema permite registrar operaciones comerciales, administrar inventario y gestionar información relevante para el funcionamiento del negocio.

📌 Descripción del Proyecto

Este proyecto fue desarrollado como parte de una práctica académica con el objetivo de implementar un sistema administrativo utilizando una arquitectura distribuida.

El sistema utiliza Java RMI (Remote Method Invocation) para la comunicación entre el cliente y el servidor, permitiendo que las interfaces gráficas del cliente interactúen con los servicios que se ejecutan en el servidor.

El servidor se encarga de:

Procesar las solicitudes del cliente

Gestionar la lógica del negocio

Acceder a la base de datos

Enviar la información requerida al cliente

🧰 Tecnologías Utilizadas

Java

Java Swing (interfaces gráficas)

Java RMI (comunicación cliente-servidor)

JDBC

MySQL (base de datos)

NetBeans (IDE de desarrollo)

🏗️ Arquitectura del Sistema

El proyecto sigue una arquitectura Cliente - Servidor con separación de capas:

Cliente

Contiene la interfaz gráfica que permite al usuario interactuar con el sistema.

Funciones principales:

Inicio de sesión

Gestión de productos

Registro de ventas

Administración de clientes

Visualización de pedidos

Servidor

Contiene la lógica del sistema y el acceso a la base de datos.

El servidor expone servicios mediante Java RMI, permitiendo que el cliente invoque métodos de forma remota.

Capas principales:

DTO (Data Transfer Objects)
Objetos utilizados para transportar datos entre cliente y servidor.

DAO (Data Access Object)
Clases encargadas de interactuar con la base de datos.

Servicios RMI
Exponen la funcionalidad del sistema para que el cliente pueda acceder a ella.

⚙️ Funcionalidades

El sistema incluye las siguientes funcionalidades principales:

Gestión de Usuarios

Inicio de sesión

Control de acceso al sistema

Gestión de Productos

Registrar productos

Editar productos

Eliminar productos

Consultar inventario

Gestión de Clientes

Registrar clientes

Consultar información de clientes

Gestión de Ventas

Registrar ventas

Generar pedidos

Control de operaciones comerciales

📂 Estructura del Proyecto




<img width="337" height="601" alt="image" src="https://github.com/user-attachments/assets/27fae3a2-0033-4bf6-b4d8-5604994e7b56" />




▶️ Cómo Ejecutar el Proyecto
1️⃣ Configurar la base de datos

Crear la base de datos en MySQL e importar las tablas necesarias.

2️⃣ Configurar la conexión

Modificar los datos de conexión en la clase:

ConexionBD.java

con los parámetros:

usuario

contraseña

nombre de la base de datos

3️⃣ Ejecutar el servidor

Ejecutar la clase principal:

ServidorMain.java

Esto iniciará el servidor RMI.

4️⃣ Ejecutar el cliente

Ejecutar la interfaz principal del cliente:

LoginFrame.java

Desde ahí se podrá acceder a todas las funcionalidades del sistema.

Cabe recalcar que este proyecto fue colaborativo con otros compañeros de la carreara.

👨‍💻 Autores:

Lenin Coutiño Lazaro

Heber Gabriel Mancilla López

Jose Manuel Cruz Martinez

Ángel Francisco Silva Cortes

Estudiante de Ingeniería en Informática interesado en el desarrollo de software, sistemas distribuidos y análisis de datos.
