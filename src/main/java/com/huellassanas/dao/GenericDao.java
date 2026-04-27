package com.huellassanas.dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico del patrón DAO (Data Access Object).
 *
 * <p>Define las operaciones CRUD estándar parametrizadas por el tipo de
 * entidad {@code T} y el tipo de su clave primaria {@code ID}. Todas las
 * implementaciones concretas deben cumplir este contrato sin exponer
 * detalles de infraestructura (SQL, JDBC) a las capas superiores.</p>
 *
 * @param <T>  tipo de la entidad del dominio
 * @param <ID> tipo de la clave primaria (generalmente {@code Integer})
 */
public interface GenericDao<T, ID> {

    /**
     * Persiste una nueva entidad en la base de datos.
     *
     * @param entity entidad a guardar (sin ID asignado por el cliente)
     * @return entidad guardada con el ID generado por la DB
     * @throws com.huellassanas.dao.DaoException si ocurre un error de persistencia
     */
    T guardar(T entity);

    /**
     * Busca una entidad por su clave primaria.
     *
     * @param id clave primaria de la entidad
     * @return {@link Optional} con la entidad si existe, vacío si no
     * @throws com.huellassanas.dao.DaoException si ocurre un error de consulta
     */
    Optional<T> buscarPorId(ID id);

    /**
     * Retorna todas las entidades de este tipo almacenadas en la DB.
     *
     * @return lista (posiblemente vacía) de entidades
     * @throws com.huellassanas.dao.DaoException si ocurre un error de consulta
     */
    List<T> listarTodos();

    /**
     * Actualiza los datos de una entidad existente.
     *
     * @param entity entidad con los nuevos valores (debe tener ID válido)
     * @return {@code true} si se actualizó al menos una fila; {@code false} si no
     * @throws com.huellassanas.dao.DaoException si ocurre un error de persistencia
     */
    boolean actualizar(T entity);

    /**
     * Elimina una entidad de la base de datos por su clave primaria.
     *
     * @param id clave primaria de la entidad a eliminar
     * @return {@code true} si se eliminó la fila; {@code false} si no existía
     * @throws com.huellassanas.dao.DaoException si ocurre un error de persistencia
     */
    boolean eliminar(ID id);
}
