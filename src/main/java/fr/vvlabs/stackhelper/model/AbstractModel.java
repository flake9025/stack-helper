package fr.vvlabs.stackhelper.model;

import java.io.Serializable;

import org.springframework.data.domain.Persistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The abstract Class AbstractModel.
 *
 * @param <K> the primary key type
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public abstract class AbstractModel<K extends Serializable> implements Persistable<K> {

	// ===========================================================
	// Fields
	// ===========================================================

	protected K id;
}
