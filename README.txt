Bais Inventory

Bais Inventory es un sistema de gestión de inventario desarrollado en JavaFX con conexión a MySQL mediante JDBC.
Permite administrar usuarios, repuestos, ensambles y reportes, facilitando el control del inventario de forma sencilla y organizada.
__________________________________________________

Tecnologías utilizadas

Lenguaje: Java 17

Interfaz gráfica: JavaFX 17 SDK

Base de datos: MySQL

Conexión: JDBC

Versionamiento: Git y GitHub

Patrón de organización: POO + paquetes (model, dao, controller, ui)
_________________________________________________________

Módulos implementados
 Gestión de usuarios

Crear usuarios (INSERT)

Consultar usuarios (SELECT)

Actualizar usuarios (UPDATE)

Eliminar usuarios (DELETE)
_____________________________________________________

 Gestión de repuestos

Registro de entradas y salidas de inventario

CRUD completo sobre la tabla repuesto
_______________________________________________________

 Gestión de ensambles

Creación de ensambles con múltiples repuestos

Consulta de ensambles existentes
_______________________________________________________
 Gestión de reportes

Generación de reportes por tipo (repuestos, ensambles, exportaciones)

Marcar reportes como vistos
______________________________________________________
Ejecución del proyecto
Clonar el repositorio:
git clone https://github.com/Ag3sag/baisinventoryjava.git
___________________________________________________

Abrir el proyecto en tu IDE.

Verificar que la base de datos baisinventory ya esté creada y con las tablas configuradas.

Revisar que la clase dao.Conexion tenga la configuración correcta:
private static final String URL = "jdbc:mysql://localhost:3306/baisinventory";
private static final String USER = "tu usuario";
private static final String PASSWORD = "tu contraseña";
___________________________________________________________
Ejecutar la clase MainApp.java. (debes tener instalado javafx-sdk-21 o superior)
__________________________________________________________________________
Base de datos MySQL

Base: bais_inventory

Usuario: root

Contraseña: root1234

Ajusta los datos en Conexion.java si tu MySQL tiene credenciales distintas.
___________________________________________________________________________
JAR sombreado (shaded) con todas las dependencias incluidas.

Contiene todos los recursos: .fxml, .css, imágenes y librerías necesarias.
______________________________________________________________________________
Ejecuta el JAR con JavaFX:
java --module-path "C:\Program Files\Java\javafx-sdk-17.0.16\lib" --add-modules javafx.controls,javafx.fxml -jar bais-inventory-1.0-SNAPSHOT-shaded.jar
Nota: Cambia la ruta de --module-path según dónde tengas instalado JavaFX.
___________________________________________________________________________________________________________________________________________
Consideraciones

Todos los archivos .fxml, .css y recursos están incluidos dentro del JAR.

Si hay problemas de conexión a la base de datos, revisa las credenciales en Conexion.java.

Compatible con Windows 10/11 con Java 17.
__________________________________________________________________