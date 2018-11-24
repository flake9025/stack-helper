package fr.vvlabs.stackhelper.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Vincent Villain Abstract Persistable Model.
 *
 * @param <K> the primary key type
 */
@MappedSuperclass
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class AbstractModel<K extends Serializable> extends AbstractPersistable<K> {
}
