package fr.vvlabs.stackhelper.mapper;

import java.io.Serializable;

import org.springframework.data.domain.Persistable;

import fr.vvlabs.stackhelper.dto.AbstractDto;

/**
 * The Interface AbstractMapper.
 *
 * @author Vincent Villain
 * Abstract Mapper for converting Model / Read DTO / Write DTO.
 * @param <T> the model type
 * @param <K> the primary key type
 * @param <S> the read dto type
 * @param <U> the create / update type
 */
public interface AbstractMapper<T extends Persistable<K>, K extends Serializable, S extends AbstractDto<K>, U extends AbstractDto<K>> {

	// ===========================================================
	// Methods
	// ===========================================================
	
	/**
	 * Convert model to read dto.
	 *
	 * @param model the model
	 * @return the s
	 */
	public S mapToDto(final T model);
	
	/**
	 * Convert write dto to model.
	 *
	 * @param writeDto the write dto
	 * @return the t
	 */
	public T mapToModel(final U writeDto);
}
