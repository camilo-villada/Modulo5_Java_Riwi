package com.ejemplo.hibernate;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App 
{
    public static void main( String[] args )
    {

        //configurar Hibernate y crear una sesión
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();

        //abrir una sesión
        Session session = sessionFactory.openSession();

        //iniciar una transacción
        Transaction transaction = session.beginTransaction();

        //crear un nuevo estudiante
        Estudiantes estudiantes = new Estudiantes("Juan Perez");
     

        //guardar el estudiante en la base de datos
        session.persist(estudiantes);


        //confirmar la transacción
        transaction.commit();

        //cerrar la sesión y la fábrica de sesiones
        session.close();
        sessionFactory.close();

        System.out.println("Estudiantes creados: " + estudiantes.getNombre());
    }
}
