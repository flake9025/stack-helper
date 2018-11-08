# Multi Tier Stack Builder Helper

- Master (1.0.0)

## Overview
This project is a Spring Boot stack builder helper, which allows fast and easy implementation of OpenAPI 3.0 REST Services.
It will help you focus on business logic instead of writing repetitive code for CRUD, Search, Paging and Sorting operations.
It uses generics types for business objects :
- Entity (Ex: Pet)
- Primary Key type (ex: Integer, Long, String, etc)
- a "Read" DTO used for read operations (ex: findAll, etc)
- a "Write" DTO used for create/update operations (ex: create, update).
  Itcan be the same as the "Read" DTO.

It will produce a Web Controller and a Service Layer, with the following operations :
- create
- create list
- count all
- find all with criterias, paging and sorting
- find by ID
- update by ID
- update list
- delete all
- delete by ID
- delete list

## Prerequisites
- JDK 8
- Maven
- Spring MVC (for REST Controllers)
- Spring 5 (for IOC and Beans)
- Spring Data 2 (for Persistence)

## Getting Started

### Entities
If you already have Spring Data Persistable entities, this is already fine.
Otherwise, you can choose to add "implements Persistable<K>" where K is your primary key type.
Or, you can choose to extend the Entity helper of this project :
- AbstractModel class : generic entity
	
for example :

```java
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class Pet extends AbstractModel<Integer> {
	private String name;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "pet_friends", joinColumns = @JoinColumn(name = "pet_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
	private List<Pet> friends;
}
```

### Dao Layer
All you have to do is to implement JpaRepository<Type, Primary Key> from Spring Data.
for example :

```java
@Repository
public interface PetDao extends JpaRepository<Pet, Integer> {
	public Pet findByName(String name);
}
```

### DTO
All you have to do is to extend the Abstract Dto class.
You can choose to use the same DTO for both Read and Write operations, or use a different one for each.
for example :

Here we have a PetDTO with full data for readings :

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetDTO extends AbstractDto<Integer> {
	private String name;
	private List<String> friends; // friends names
}
```
And a PetWriteDTO with different / less data, for create and update operations :

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetWriteDTO extends AbstractDto<Integer> {
	private String name;
	private List<Integer> friendsIds; // friends ids
}
```

### Mappers
You will need mappers to walk from an Entity to a DTO, and vice versa.
Il you are familiar with MapStruct, all you have to do is to create an interface and extend the abstract Mapper class
for example :

```java
@Mapper(componentModel="spring")
public interface PetMapper extends AbstractMapper<Pet, Integer, PetDTO> {
	@Override
	public PetDTO mapToDto(Pet model);
}
```

If you don't want to use MapStruct, you can still write your own mapper class :

```java
@Component
public class PetMapperImpl implements AbstractMapper<Pet, Integer, PetDTO> {
	@Override
	public PetDTO mapToDto(Pet model){
		PetDTO dto = new PetDTO();
		dto.setId(model.getId());
		dto.setName(model.getName());
		for(Pet pet : model.getFriends()){
			dto.addFriend(pet.getName());
		}
		return dto;
	}
}
```

### Service Layer
All you have to do is to extend the abstract Service class, and fill the "updateModel" method with business logic.
for example :

```java
@Service
public class PetService extends AbstractService<Pet, Integer, PetDTO, PetWriteDTO> {

	@Autowired
	private PetDao petDao;
	
	@Override
	protected void updateModel(Pet model, PetWriteDTO dto) {
		// update id ?
		if(dto.getId() != null) {
			model.setId(dto.getId());
		}
		// check name, should be unique !
		if(!dto.getName().isEmpty() && petDao.findByName(dto.getName()) == null) {
			model.setName(dto.getName());
		}
		// check friends
		if(!CollectionUtils.isEmpty(dto.getFriendsIds())) {
			for(Integer friendId : dto.getFriendsIds()){
				// pet friend should exists !
				if(petDao.existsById(friendId)){
					model.getFriends().add(petDao.findById(friendId).get());
				}
			}
		}
	}
}
```

### Controller layer
All you have to do is to extend the abstract Controller class.
Due to QueryDSL current limitations and type erasure, you have to override the "findAll" operation,
in order to give "QuerydslPredicate" the entity type.
for example :

```java
@RestController
@RequestMapping("/pets")
@Api
public class PetController extends AbstractRestController<Pet, Integer, PetDTO, PetWriteDTO> {
	@Override
	@GetMapping(params = { "page", "size" })
	public ResponseEntity<Page<PetDTO>> findAll( //
			@QuerydslPredicate(root = Pet.class) Predicate predicate, //
			@RequestParam(value = "page", required = false, defaultValue = "0") int page, //
			@RequestParam(value = "size",  required = false, defaultValue = "30") int size, //
			@RequestParam(value = "sort",  required = false) String sort //
			) { //
		return super.findAll(predicate, page, size, sort);
	}
}
```

### Demo Project
See the demo project with beautiful Pets :
https://github.com/flake9025/stack-helper-demo

## Examples

http://localhost:8080/pets

```json
{"content":[{"id":1,"name":"Cat","friends":null},{"id":2,"name":"Dog","friends":["Poney","Fish"]},{"id":3,"name":"Poney","friends":["Dog","Fish"]},{"id":4,"name":"Fish","friends":["Dog","Poney"]}],"pageable":{"sort":{"sorted":false,"unsorted":true},"offset":0,"pageSize":30,"pageNumber":0,"paged":true,"unpaged":false},"last":true,"totalPages":1,"totalElements":4,"size":30,"number":0,"sort":{"sorted":false,"unsorted":true},"numberOfElements":4,"first":true}
```

http://localhost:8080/pets?name=Dog&page=0

```json
{"content":[{"id":2,"name":"Dog","friends":["Poney","Fish"]}],"pageable":{"sort":{"sorted":false,"unsorted":true},"offset":0,"pageSize":30,"pageNumber":0,"paged":true,"unpaged":false},"last":true,"totalPages":1,"totalElements":1,"size":30,"number":0,"sort":{"sorted":false,"unsorted":true},"numberOfElements":1,"first":true}
```
