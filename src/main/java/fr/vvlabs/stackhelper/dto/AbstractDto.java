package fr.vvlabs.stackhelper.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The abstract Class AbstractDto.
 *
 * @param <K> the primary key type
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@ToString
public abstract class AbstractDto<K extends Serializable> implements Serializable {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final long serialVersionUID = 7700904699389056570L;
	
	// ===========================================================
	// Fields
	// ===========================================================

    protected K id;
}