package com.app.dao.impl;

import com.app.dao.ProductoDAO;
import com.app.model.entity.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl extends GenericDAOImpl<Producto, Integer> implements ProductoDAO {

    private static final String INSERT = "INSERT INTO productos (nombre, precio) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE productos SET nombre=?, precio=? WHERE id=?";
    private static final String DELETE = "DELETE FROM productos WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM productos WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM productos ORDER BY id";

    private static final String FIND_BY_NOMBRE = "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY id";
    private static final String FIND_BY_PRECIO_BETWEEN =
            "SELECT * FROM productos WHERE precio BETWEEN ? AND ? ORDER BY precio, id";

    @Override
    protected Producto mapRow(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio")
        );
    }

    @Override protected String getInsertSQL() { return INSERT; }
    @Override protected String getUpdateSQL() { return UPDATE; }
    @Override protected String getDeleteSQL() { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL() { return FIND_ALL; }

    @Override
    protected void setInsertParams(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setBigDecimal(2, p.getPrecio());
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setBigDecimal(2, p.getPrecio());
        ps.setInt(3, p.getId());
    }

    @Override
    protected void setDeleteParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected void setFindByIdParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    public Producto save(Producto p) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParams(ps, p);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Error en save(Producto)", e);
        }
    }

    @Override
    public List<Producto> findByNombre(String nombre) {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NOMBRE)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByNombre(Producto)", e);
        }
        return list;
    }

    @Override
    public List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max) {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PRECIO_BETWEEN)) {

            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByPrecioBetween(Producto)", e);
        }
        return list;
    }
}

