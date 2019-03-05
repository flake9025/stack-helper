package fr.vvlabs.stackhelper.dao;

import java.io.Serializable;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Vincent Villain Abstract DAO with CRUD and Search operations
 *
 * @param <T> the model type
 * @param <K> the primary key type
 */
public interface AbstractDAO<T extends Persistable<K>, K extends Serializable> extends JpaRepository<T, K> {

}
