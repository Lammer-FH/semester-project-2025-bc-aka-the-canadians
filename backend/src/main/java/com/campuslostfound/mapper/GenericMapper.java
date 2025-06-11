package com.campuslostfound.mapper;

import java.util.List;

/**
 * Generische Schnittstelle für MapStruct-Mapper.
 *
 * @param <E> Der Entity-Typ
 * @param <D> Der DTO-Typ
 */
public interface GenericMapper<E, D> {

    /**
     * Konvertiert eine Entität in ein DTO.
     *
     * @param entity Die zu konvertierende Entität
     * @return Das resultierende DTO
     */
    D toDTO(E entity);

    /**
     * Konvertiert ein DTO in eine Entität.
     *
     * @param dto Das zu konvertierende DTO
     * @return Die resultierende Entität
     */
    E toEntity(D dto);

    /**
     * Konvertiert eine Liste von Entitäten in eine Liste von DTOs.
     *
     * @param entities Die zu konvertierenden Entitäten
     * @return Eine Liste der konvertierten DTOs
     */
    List<D> toDTOList(List<E> entities);

    /**
     * Konvertiert eine Liste von DTOs in eine Liste von Entitäten.
     *
     * @param dtos Die zu konvertierenden DTOs
     * @return Eine Liste der konvertierten Entitäten
     */
    List<E> toEntityList(List<D> dtos);
}
