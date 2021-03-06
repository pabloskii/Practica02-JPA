/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entities.Empleado;
import entities.Historial;
import entities.Incidencia;
import exception.exceptionJPA;
import static java.lang.Math.log;
import static java.lang.StrictMath.log;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.AssertFalse;

/**
 *
 * @author pablourbano
 */
@Stateless
public class incidenciasEJB {

    @PersistenceUnit
    EntityManagerFactory emf;

    public void altaEmpleado(Empleado e) throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        //Comprobamos si ya existe un empleado
        Empleado aux = em.find(Empleado.class, e.getNombreusuario());
        if (aux != null) {
            em.close();
            throw new exceptionJPA("Ya existe un empleado con este nombre");
        }
        em.persist(e);
        em.close();
    }

    public boolean validarEmpleado(String nombre, String pass) throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        //comprobamos que exista el empleado
        Empleado aux = em.find(Empleado.class, nombre);
        if (aux != null) {
            if (pass.equals(aux.getPassword())) {
                em.close();
                return true;
            }
        } else {
            em.close();
            throw new exceptionJPA("Este usuario no existe");

        }
        em.close();
        return false;
    }

    public Empleado buscarEmpleado(String nombre) {
        EntityManager em = null;
        em = emf.createEntityManager();
        Empleado e2;
        e2 = em.find(Empleado.class, nombre);
        em.close();
        return e2;

        /*
        Empleado e = new Empleado();
        Query q;
        q = emf.createEntityManager().createQuery("SELECT e FROM Empleado e WHERE e.nombreusuario = :empleado");
        q.setParameter("empleado", nombre);
        return (Empleado) q.getResultList();*/
    }

    public void cambiarContra(Empleado e) {
        Query q;
        q = emf.createEntityManager().createQuery("UPDATE Empleado SET password= :pas WHERE nombreusuario= :nombr");
        q.setParameter("nombr", e.getNombreusuario());
        q.setParameter("pas", e.getPassword());
        q.executeUpdate();
    }

    public void eliminarEmpleado(String username) throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        Empleado e = em.find(Empleado.class, username);
        if (e == null) {
            em.close();
            throw new exceptionJPA("El empleado no existe");
        }
        em.remove(e);
        em.close();
    }

    public List<Incidencia> incidenciasEnviadas(Empleado e) {
        Query q;
        q = emf.createEntityManager().createQuery("SELECT i FROM Incidencia i WHERE i.origen = :empleado");
        q.setParameter("empleado", e);
        return q.getResultList();
    }

    public List<Incidencia> incidenciasRecividas(Empleado e) {
        Query q;
        q = emf.createEntityManager().createQuery("SELECT i FROM Incidencia i WHERE i.destino = :empleado");
        q.setParameter("empleado", e);
        return q.getResultList();
    }

    public List<Incidencia> listadoIncidencias() {
        Query q;
        q = emf.createEntityManager().createNamedQuery("Incidencia.findAll");
        return q.getResultList();

    }

    public List<Empleado> listadoEmpleados() {
        Query q;
        q = emf.createEntityManager().createNamedQuery("Empleado.findAll");
        return q.getResultList();

    }

    public void crearIncidencias(Incidencia e) throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        //Comprobamos si ya existe un empleado
        if (e.getDestino() != null && e.getDetalle() != null && e.getFechahora() != null && e.getIdincidencia() != null && e.getOrigen() != null && e.getTipo() != null) {
            em.persist(e);
            em.close();
        } else {
            System.out.println(e.getDestino());
            em.close();
        }
    }

    public List<Empleado> rankingEmpleados() {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT h.empleado FROM Historial h WHERE h.tipo='U' GROUP BY h.empleado ORDER BY count(h)");        
        List<Empleado> a = q.getResultList();
        em.close();
        return a;
    }

    public List<Empleado> todosEmpleados() throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        return em.createNamedQuery("Empleado.findAll").getResultList();
    }

    public Integer maximoIdIncidencia() {
        Integer a;
        Query q;
        q = emf.createEntityManager().createQuery("SELECT max(i.idincidencia) FROM Incidencia i");

        if (q.getSingleResult() == null) {
            a = 0;
            return a;
        } else {
            a = (int) q.getSingleResult();
            return a;
        }
    }

    public int miPosicions(Empleado e) throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        List<Empleado> empleados = em.createQuery("SELECT h.empleado FROM Historial h WHERE h.tipo='U' GROUP BY h.empleado ORDER BY count(h)").getResultList();
        int i = 1;
        for (Empleado empleado : empleados) {
            if (e.getNombreusuario().equals(e.getNombreusuario())) {
                em.close();
                return i;
            }
            i++;
        }
        em.close();
        throw new exceptionJPA("El usuario no se ha logeado nunca");
    }

    public void crearEvento(Historial h) throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        //Comprovamos si existe empleado
        Empleado aux = em.find(Empleado.class, h.getEmpleado().getNombreusuario());
        if (aux == null) {
            em.close();
            throw new exceptionJPA("El empleado no existe");
        }
        em.persist(h);
        em.close();
    }

    public void modificarEmpleado(Empleado e) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("UPDATE Empleado SET nombrecompleto= :nombrec, telefono= :tel, ciudad= :ciu WHERE nombreusuario= :nombreu");
        q.setParameter("nombrec", e.getNombrecompleto());
        q.setParameter("tel", e.getTelefono());
        q.setParameter("ciu", e.getCiudad());
        q.setParameter("nombreu", e.getNombreusuario());
        q.executeUpdate();
        em.close();
    }

    public Historial ultimaConec(Empleado e) throws exceptionJPA {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT h FROM Historial h WHERE h.tipo='I' AND h.empleado = :empleado ORDER BY h.fechahora DESC");
        q.setParameter("empleado", e);
        if (q.getResultList().isEmpty()) {
            em.close();
            throw new exceptionJPA("El usuario no se ha logeado nunca");
        }
        Historial a =  (Historial) q.getResultList().get(0);
        em.close();
        return a;
    }
}
