package com.huellassanas.dao;

import com.huellassanas.model.Usuario;
import java.util.Optional;

/**
 * Contrato DAO específico para la entidad {@link Usuario}.
 *
 * <p>Extiende {@link GenericDao} con operaciones de consulta propias
 * de la lógica de autenticación y gestión de usuarios del sistema.</p>
 */
public interface UsuarioDao extends GenericDao<Usuario, Integer> {

    /**
     * Busca un usuario por su nombre de usuario único.
     *
     * @param username nombre de usuario
     * @return {@link Optional} con el usuario si existe
     */
    Optional<Usuario> buscarPorUsername(String username);

    /**
     * Verifica las credenciales de acceso al sistema.
     *
     * <p>Compara el hash SHA-256 de {@code plainPassword} contra
     * el {@code password_hash} almacenado en la base de datos.</p>
     *
     * @param username      nombre de usuario
     * @param plainPassword contraseña en texto plano (se hashea internamente)
     * @return {@link Optional} con el usuario autenticado, o vacío si falla
     */
    Optional<Usuario> autenticar(String username, String plainPassword);
}
