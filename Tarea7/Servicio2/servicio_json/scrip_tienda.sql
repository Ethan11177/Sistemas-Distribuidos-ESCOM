CREATE DATABASE servicio_tienda;
USE servicio_tienda;
create table articulos
(
    id_articulo INTEGER AUTO_INCREMENT PRIMARY KEY,
    nombre_articulo VARCHAR(100) NOT NULL,
    descripcion_articulo VARCHAR(100) NOT NULL,
    precio_articulo DOUBLE NOT NULL,
    cantidad_articulo INTEGER NOT NULL,
    foto_articulo LONGBLOB
);
create table carrito_compras
(
    id_articulo INTEGER,
    cantidad_carrito INTEGER
);

/*
iptables -t nat -A OUTPUT -o lo -p tcp --dport 80 -j REDIRECT --to-port 8080

/*
javac -cp $CATALINA_HOME/lib/javax.ws.rs-api-2.0.1.jar:$CATALINA_HOME/lib/gson-2.3.1.jar:. servicio_json/Servicio.java
rm WEB-INF/classes/servicio_json/*
rm WEB-INF/classes/servicio_url/*
cp servicio_json/*.class WEB-INF/classes/servicio_json/.
jar cvf Servicio.war WEB-INF META-IN
cp Servicio.war /home/Ethan117/apache-tomcat-8.5.84//webapps/
*/
