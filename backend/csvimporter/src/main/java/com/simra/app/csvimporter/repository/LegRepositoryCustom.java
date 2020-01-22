package com.simra.app.csvimporter.repository;

import com.simra.app.csvimporter.model.LegEntity;

import java.util.List;

public interface LegRepositoryCustom {
    List<LegEntity> findByGeometryIntersection(LegEntity legEntity);
}