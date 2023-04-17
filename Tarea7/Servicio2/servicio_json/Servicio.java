/*
  Servicio.java
  Servicio web tipo REST
  Recibe parámetros utilizando JSON
  Carlos Pineda Guerrero, noviembre 2022
*/
package servicio_json;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.google.gson.*;

import servicio_json.Articulo;
import servicio_json.ParamBorrarArticulo;
import servicio_json.ParamModificaArticulo;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio

@Path("ws")
public class Servicio {

  static DataSource pool = null;

  static {
    try {
      Context ctx = new InitialContext();
      pool = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64())
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

  // aqui empieza la parte que ingresa los datos de los articulos a la base de
  // datos
  @POST
  @Path("captura_articulo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response alta(String json) throws Exception {

    ParamAltaArticulo p = (ParamAltaArticulo) j.fromJson(json, ParamAltaArticulo.class);
    Articulo articulo = p.articulo;

    if (articulo.nombre == null || articulo.nombre.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre del articulo "))).build();

    if (articulo.descripcion == null || articulo.descripcion.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la descripcion"))).build();

    if (articulo.cantidad <= 0)
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la cantidad"))).build();

    if (articulo.precio <= 0)
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el precio"))).build();

    Class.forName("com.mysql.jdbc.Driver");
    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/servicio_tienda", "hugo", "hugo");

    try {

      conexion.setAutoCommit(false);

      PreparedStatement stmt_1 = conexion.prepareStatement(
          "INSERT INTO articulos (id_articulo, nombre_articulo, descripcion_articulo, precio_articulo, cantidad_articulo, foto_articulo) VALUES (0,?,?,?,?,?)");

      try {
        stmt_1.setString(1, articulo.nombre);
        stmt_1.setString(2, articulo.descripcion);
        stmt_1.setDouble(3, articulo.precio);
        stmt_1.setInt(4, articulo.cantidad);

        if (articulo.foto != null) {
          stmt_1.setBytes(5, articulo.foto);
        } else {
          stmt_1.setNull(5, Types.BLOB);
        }

        stmt_1.executeUpdate();
      } finally {
        stmt_1.close();
      }

      conexion.commit();
    } catch (Exception e) {
      conexion.rollback();
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.setAutoCommit(true);
      conexion.close();
    }
    return Response.ok().build();
  }
  //////////////////////////////////////////////////////////////////////////////////////////////

  // aqui empieza la consula de la base de datos para el carrito
  @POST
  @Path("consulta_articulo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consulta(String json) throws Exception {
    ParamConsultaArticulo p = (ParamConsultaArticulo) j.fromJson(json, ParamConsultaArticulo.class);
    String nombre = p.nombre;
    String descripcion = p.descripcion;

    Class.forName("com.mysql.jdbc.Driver");
    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/servicio_tienda", "hugo", "hugo");

    try {
      PreparedStatement stmt_1 = conexion.prepareStatement(
          "SELECT id_articulo, nombre_articulo, descripcion_articulo, precio_articulo, cantidad_articulo, foto_articulo FROM articulos WHERE nombre_articulo LIKE ? OR descripcion_articulo LIKE ?");
      try {
        stmt_1.setString(1, nombre);
        stmt_1.setString(2, descripcion);

        ResultSet rs = stmt_1.executeQuery();
        try {
          if (rs.next()) {
            Articulo r = new Articulo();
            r.id_articulo = rs.getInt(1);
            r.nombre = rs.getString(2);
            r.descripcion = rs.getString(3);
            r.precio = rs.getDouble(4);
            r.cantidad = rs.getInt(5);
            r.foto = rs.getBytes(6);
            return Response.ok().entity(j.toJson(r)).build();
          }
          return Response.status(400).entity(j.toJson(new Error("El nombre y/o descripcion no existen"))).build();
        } finally {
          rs.close();
        }
      } finally {
        stmt_1.close();
      }
    } catch (Exception e) {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.close();
    }
  }
  ////////////////////////////////////////////////////////////////////

  // Aqui se inicia la modificacion del usuario, esta seccion va a ser modificada
  // para que sirva como ingreso de los datos del articulo al carrito//
  @POST
  @Path("modifica_articulo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response modifica(String json) throws Exception {
    ParamModificaArticulo p = (ParamModificaArticulo) j.fromJson(json, ParamModificaArticulo.class);
    int id_articulo = p.id_articulo;
    int cantidad = p.cantidad;
    int cant_pro = 0, totcantidad = 0;

    Class.forName("com.mysql.jdbc.Driver");
    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/servicio_tienda", "hugo", "hugo");

    try {

      conexion.setAutoCommit(false);

      PreparedStatement stmt_2 = conexion
          .prepareStatement("SELECT cantidad_articulo FROM articulos WHERE id_articulo LIKE ?");
      try {
        stmt_2.setInt(1, id_articulo);

        ResultSet rs = stmt_2.executeQuery();
        try {
          if (rs.next()) {
            cant_pro = rs.getInt(1);
          }

        } finally {
          rs.close();
        }
      } finally {
        stmt_2.close();
      }

      if (cant_pro == 0) {
        return Response.status(400).entity(j.toJson(new Error("Ya no se tienen existencias del producto"))).build();
      }

      if ((cant_pro - cantidad) >= 0) {
        Math.abs(totcantidad = cant_pro - cantidad);
      } else if ((cant_pro - cantidad) < 0) {
        totcantidad = 0;
        Math.abs(cantidad = cantidad - (cantidad - cant_pro));
      }

      PreparedStatement stmt_3 = conexion
          .prepareStatement("UPDATE articulos SET cantidad_articulo=? WHERE id_articulo=?");
      try {
        stmt_3.setInt(1, totcantidad);
        stmt_3.setInt(2, id_articulo);

        stmt_3.executeUpdate();
      } finally {
        stmt_3.close();
      }

      PreparedStatement stmt_1 = conexion
          .prepareStatement("INSERT INTO carrito_compras(id_articulo, cantidad_carrito) VALUES (?,?)");
      try {
        stmt_1.setInt(1, id_articulo);
        stmt_1.setInt(2, cantidad);

        stmt_1.executeUpdate();
      } finally {
        stmt_1.close();
      }

      conexion.commit();
    } catch (Exception e) {
      conexion.rollback();
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.setAutoCommit(true);
      conexion.close();
    }
    return Response.ok().build();
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////// aqui se va a iniciar la ultima pantalla para
  ////////////////////////////////// comprar los articulos
  @POST
  @Path("consulta_carrito")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response borra(String json) throws Exception {

    ArrayList<ParamBorrarArticulo> lista = new ArrayList<ParamBorrarArticulo>();

    Class.forName("com.mysql.jdbc.Driver");
    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/servicio_tienda", "hugo", "hugo");

    try {

      PreparedStatement stmt_1 = conexion.prepareStatement(
          "SELECT A.id_articulo, A.cantidad_carrito, B.precio_articulo, B.foto_articulo, B.nombre_articulo FROM carrito_compras A INNER JOIN articulos B ON A.id_articulo = B.id_articulo");

      try {

        ResultSet rs = stmt_1.executeQuery();
        try {

          while (rs.next()) {

            ParamBorrarArticulo r = new ParamBorrarArticulo();
            r.id_articulo = rs.getInt(1);
            r.cantidad = rs.getInt(2);
            r.precio = rs.getDouble(3);
            r.foto = rs.getBytes(4);
            r.nombre = rs.getString(5);
            lista.add(r);

          }
        } catch (Exception e) {
          return Response.status(400).entity(j.toJson(new Error(e.getMessage() + " este es le mensaje 1"))).build();
        } finally {
          rs.close();
        }
      } finally {
        stmt_1.close();
      }
    } catch (Exception e) {
      conexion.rollback();
      return Response.status(400).entity(j.toJson(new Error(e.getMessage() + " este es el mensaje 2"))).build();
    } finally {
      conexion.close();
    }

    return Response.ok().entity(j.toJson(lista)).build();
  }

  @POST
  @Path("borrar_articulo_carrito")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response borrar_articulo(String json) throws Exception {
    ParamModificaArticulo p = (ParamModificaArticulo) j.fromJson(json, ParamModificaArticulo.class);
    int id_articulo = p.id_articulo;
    int cantidad = p.cantidad;
    int cant_pro = 0;
    int cantot;

    Class.forName("com.mysql.jdbc.Driver");
    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/servicio_tienda", "hugo", "hugo");

    try {

      conexion.setAutoCommit(false);

      PreparedStatement stmt_2 = conexion
          .prepareStatement("SELECT cantidad_articulo FROM articulos WHERE id_articulo LIKE ?");
      try {
        stmt_2.setInt(1, id_articulo);

        ResultSet rs = stmt_2.executeQuery();
        try {
          if (rs.next()) {
            cant_pro = rs.getInt(1);
          }

        } finally {
          rs.close();
        }
      } finally {
        stmt_2.close();
      }

      cantot = cantidad + cant_pro;

      PreparedStatement stmt_3 = conexion
          .prepareStatement("UPDATE articulos SET cantidad_articulo=? WHERE id_articulo=?");
      try {
        stmt_3.setInt(1, cantot);
        stmt_3.setInt(2, id_articulo);

        stmt_3.executeUpdate();
      } finally {
        stmt_3.close();
      }

      PreparedStatement stmt_1 = conexion
          .prepareStatement("DELETE FROM carrito_compras WHERE id_articulo=? AND cantidad_carrito=?");
      try {
        stmt_1.setInt(1, id_articulo);
        stmt_1.setInt(2, cantidad);

        stmt_1.executeUpdate();
      } finally {
        stmt_1.close();
      }

      conexion.commit();
    } catch (Exception e) {
      conexion.rollback();
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.setAutoCommit(true);
      conexion.close();
    }
    return Response.ok().build();
  }

  @POST
  @Path("borrar_el_carrito")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response borrar_carrito(String json) throws Exception {

    ArrayList<ParamModificaArticulo> lista = new ArrayList<ParamModificaArticulo>();
    int cantot = 0;

    Class.forName("com.mysql.jdbc.Driver");
    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/servicio_tienda", "hugo", "hugo");

    try {

      conexion.setAutoCommit(false);

      PreparedStatement stmt_1 = conexion.prepareStatement("DELETE FROM carrito_compras");
      try {
        stmt_1.executeUpdate();
      } finally {
        stmt_1.close();
      }

      conexion.commit();
    } catch (Exception e) {
      conexion.rollback();
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.setAutoCommit(true);
      conexion.close();
    }
    return Response.ok().build();
  }
}
