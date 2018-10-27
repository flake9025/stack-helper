package fr.vvlabs.stackhelper.mapper;

/**
 * @author Vincent Villain
 * Abstract Mapper for converting Model / Read DTO / Write DTO.
 *
 * @param <T> the model type
 * @param <S> the read dto type
 * @param <U> the create / update type
 */
public interface AbstractMapper<T, S, U> {

	/**
	 * Convert model to read dto.
	 *
	 * @param model the model
	 * @return the s
	 */
	public S mapToDto(T model);
	
	/**
	 * Convert write dto to model.
	 *
	 * @param writeDto the write dto
	 * @return the t
	 */
	public T mapToModel(U writeDto);
}
