package fr.vvlabs.stackhelper.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import fr.vvlabs.stackhelper.mapper.AbstractMapper;

/**
 * @author Vincent Villain Abstract Service Layer for CRUD operations
 *
 * @param <T> the model type
 * @param <K> the primary key type
 * @param <S> the Read DTO type
 * @param <U> the Create/Update DTO type
 */
public abstract class AbstractService<T, K, S, U> {

	// ===========================================================
	// Fields
	// ===========================================================

	@Autowired
	private CrudRepository<T, K> dao;

	@Autowired
	private AbstractMapper<T, S, U> mapper;

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Count all.
	 *
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public long countAll() {
		return dao.count();
	}

	/**
	 * Find all.
	 *
	 * @return the read dto list
	 */
	@Transactional(readOnly = true)
	public List<S> findAll() {
		return StreamSupport.stream(dao.findAll().spliterator(), false).map(mapper::mapToDto).collect(Collectors.toList());
	}

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the read dto
	 */
	@Transactional(readOnly = true)
	public S findById(K id) {
		S dto = null;
		Optional<T> model = dao.findById(id);
		if (model.isPresent()) {
			dto = mapper.mapToDto(model.get());
		}
		return dto;
	}

	/**
	 * Save.
	 *
	 * @param dto the create/update dto
	 * @return the created/updated model
	 */
	@Transactional
	public T save(U dto) {
		return dao.save(mapper.mapToModel(dto));
	}

	/**
	 * Save all.
	 *
	 * @param dtoList the create/update dto list
	 * @return the created/updated models
	 */
	public List<T> saveAll(List<U> dtoList) {
		Iterable<T> models = dao.saveAll(dtoList.stream().map(mapper::mapToModel).collect(Collectors.toList()));
		return StreamSupport.stream(models.spliterator(), false).collect(Collectors.toList());
	}

	/**
	 * Delete.
	 *
	 * @param model the model
	 */
	@Transactional
	public void delete(T model) {
		dao.delete(model);
	}

	/**
	 * Delete by id.
	 *
	 * @param id the id
	 */
	@Transactional
	public void deleteById(K id) {
		dao.deleteById(id);
	}

	/**
	 * Delete by id list.
	 *
	 * @param idList the id list
	 */
	@Transactional
	public void deleteByIdList(List<K> idList) {
		idList.forEach(dao::deleteById);
	}
}
