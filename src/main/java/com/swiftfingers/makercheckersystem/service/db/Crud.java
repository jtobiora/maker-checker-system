package com.swiftfingers.makercheckersystem.service.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface Crud<T, ID extends Serializable> {

    public T create(T t);

    public T update(T t);

    public void delete(T t);

    public Optional<T> findById(ID id);

    default Page<T> query(T t, PageRequest p) { return null;}

    public boolean exists(T t, ID id);

    default Long count(T t) {
        return null;
    }

    default Collection<T> list() {
        return Collections.emptyList();
    }

    default List<T> findAll(ID... ids) {
        return Collections.emptyList();
    }

    default void deleteAll(Collection<T> t) {
    }

    default Page<T> findAll(T t, PageRequest p) {
        return null;
    }

    default Page<T> advanceSearch(T t, PageRequest p) {
        return null;
    }

    default Page<T> single(String... args) {
        return null;
    }
}

