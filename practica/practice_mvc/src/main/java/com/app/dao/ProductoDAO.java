package com.app.dao;

import com.app.model.entity.Producto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoDAO extends GenericDAO<Producto, Integer> {
    List<Producto> findByNombre(String nombre);
    List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);
}

