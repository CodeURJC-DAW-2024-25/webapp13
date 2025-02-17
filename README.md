# LibroRed - Aplicación Web de Préstamo de Libros entre Particulares

---

## Integrantes del Equipo de Desarrollo (Equipo 13)

- **Nombre**: Ana María  
- **Apellidos**: Jurado Crespo  
- **Correo oficial de la universidad**: am.juradoc@alumnos.urjc.es  
- **Cuenta en GitHub**: [medinaymedia](https://github.com/medinaymedia)  
---
- **Nombre**: Jesús  
- **Apellidos**: Ortiz Lopo  
- **Correo oficial de la universidad**: j.ortizl.2019@alumnos.urjc.es  
- **Cuenta en GitHub**: [JesusOrtiz10](https://github.com/JesusOrtiz10)

---
---

## Descripción de la Aplicación

### 1. Entidades de la Aplicación

1. **Usuario**  
   Representa a los usuarios de la plataforma, que pueden ser **prestamistas** y/o **prestatarios**.
   Prestamiesta si tiene libros a disposición para ser prestados.
   Prestatario si es la persona que recibe un libro en préstamo.
   A su vez hay tres tipos de usuarios: Anónimo, Registrado y Admin. 

2. **Libro**  
   Representa los libros disponibles para préstamo.

3. **Préstamo**  
   Representa el proceso de préstamo de un libro entre dos usuarios.

4. **Valoración**  
   Valoración del libro.

#### Relaciones entre las Entidades

- **Usuario ↔ Libro**: Un usuario puede tener varios libros que ofrece en préstamo.
- **Usuario ↔ Préstamo**: Un préstamo conecta a dos usuarios: el **prestamista** (dueño del libro) y el **prestatario** (quien recibe el libro).
- **Préstamo ↔ Libro**: Cada préstamo está asociado a un libro específico.
- **Usuario ↔ Valoración**: Tras finalizar un préstamo, el usuario prestatario puede evaluar el libro con una puntuación de 1 a 5.

---

### 2. Permisos de los Usuarios

- **Usuario Anónimo**: Puede consultar información básica de los libros disponibles, pero no puede realizar préstamos ni valoraciones.
- **Usuario Registrado**: Puede ofrecer libros en préstamo, solicitar préstamos, y valorar los libros que ha tomado prestados. Es dueño de sus propios libros y préstamos.
- **Usuario Administrador**: Tiene control total sobre la información de la web, incluyendo la gestión de usuarios y libros.

---

### 3. Imágenes

- **Usuario**: Cada usuario tendrá una única imagen de perfil (avatar).
- **Libro**: Cada libro tendrá asociada una única imagen de la portada.

---

### 4. Gráficos

Cada usuario podrá visualizar:

1. **Evolución de Préstamos Realizados (como Prestamista)**: Gráfico de líneas que muestra el número de libros prestados por mes.
2. **Evolución de Préstamos Recibidos (como Prestatario)**: Gráfico de líneas que muestra el número de libros recibidos por mes.
3. **Gráfico de Préstamos por Género**: Gráfico de barras que representa el número de libros de cada género que se han prestado en el último mes.

---

### 5. Tecnología Complementaria
Tenemos dos propuestas. El desarrollo de una u otra se basará en el número de integrantes que participen finalmente en el Equipo de Desarrollo.

1. **API REST para Información de Libros**:  
   Utilizaremos la API de **Open Library** (https://openlibrary.org/developers/api) para obtener información detallada de los libros mediante el ISBN.

2. **Descarga en PDF de Préstamos**:  
   Generación de un PDF con los detalles de los préstamos activos e inactivos de cada usuario usando **jsPDF** o **PDFMake**.

---

### 6. Algoritmo o Consulta Avanzada

**Algoritmo de Filtrado Colaborativo para Recomendación de Libros**:

- **Funcionamiento**:
  1. **Recolección de Datos**: Se recopilan los datos de los préstamos y valoraciones de los libros realizadas por los usuarios.
  2. **Identificación de Preferencias**: El sistema analiza los atributos de los libros que han sido valorados positivamente por el usuario (género y vaoración)
  3. **Evaluación de Usuarios y Libros Disponibles**: Se identifican usuarios con libros que coinciden en este atributo.
  4. **Recomendación**: El sistema sugiere libros alineados con los intereses del usuario.



Este algoritmo ofrecerá recomendaciones personalizadas y confiables, mejorando la experiencia del usuario al ayudarle a encontrar libros bien valorados por otros lectores.
