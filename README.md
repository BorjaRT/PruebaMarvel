# Marvel DB

La aplicación Marvel DB permite realizar la consulta del listado de personajes Marvel disponibles a través del API de Marvel Cómics, así como de la vista de detalle de cada uno de ellos.

## Funcionamiento de la aplicación

Al iniciar la aplicación se realizará la carga de los primeros 40 personajes del listado. Al llegar al final de la lista se repetirá la llamada al servicio cargando 20 personajes adicionales hasta que no haya más personajes disponibles.

### Detalle de personaje

Al pulsar en un personaje de la lista se accederá a una nueva pantalla en la que se mostrará el nombre, imagen y descripción del personaje. Así como un listado de las series, comics e historias en las que ha aparecido el mismo.
La disponibilidad de estos datos varía de un personaje a otro.

### Filtro

Seleccionando la opción "Filtro" (icono de embudo) en la barra superior se desplegará un panel con un campo de edición de texto en el que el usuario podrá introducir el terxto por el que quiere filtrar los personajes de la lista.
Hay que tener en cuenta que **este filtro sólo se aplica a la lista general de personajes, no a los resultados de búsqueda por nombre**.

### Búsqueda

Seleccionando la opción "Buscar" (icono de lupa) en la barra superior se desplegará un panel con un campo de edición de texto en el que el usuario podrá introducir el texto que quiere buscar mediante el API de personajes.
Hay que tener en cuenta que al realizar la búsqueda (pulsando en la lupa en el panel desplegado) se realizará una petición buscando los personajes **cuyo nombre empiece por la cadena de texto introducida**.

## Consideraciones técnicas

### Librerías

Debido a que ser realizan peticiones sencillas con todos los datos necesarios para la consulta integrados como parte de la URL de la petición, se ha optado por la librería [**Volley**](https://developer.android.com/training/volley/) para realizar las consultas a servicios.

Se ha utilizado [**GSON**](https://github.com/google/gson) como librería para trabajar con los datos en formato JSON.
