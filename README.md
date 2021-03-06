# Multi Tier Stack Builder Helper

- Master (1.0.0)

## Overview
This project is a Spring Boot stack builder helper, which allows fast and easy implementation of OpenAPI 3.0 REST Services.
It will help you focus on business logic instead of writing repetitive code for CRUD, Filtering, Paging and Sorting operations.
It uses generics types for business objects :
- Entity (Ex: Pet)
- Primary Key type (ex: Integer, Long, String, etc)
- a "Read" DTO used for read operations (ex: findAll, etc)
- a "Write" DTO used for create/update operations (ex: create, update).
  It can be the same as the "Read" DTO.

It will produce a Web Controller and a Service Layer, with the following operations :
- create
- create list
- count all
- find all with query language, paging and sorting
- find by ID
- update by ID
- update list
- delete all
- delete by ID
- delete list

## Prerequisites
- JDK 8 
- Maven

## Frameworks used
- Spring IOC, Spring MVC, Spring Data
- QueryDSL
- Lombok
- Slf4j

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
	@Column
	private String name;
	@Column
	private int age;
	@Column
	private boolean male;
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "pet_friends", joinColumns = @JoinColumn(name = "pet_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
	private List<Pet> friends;
}
```

### Dao Layer
All you have to do is to extend AbstractDAO<Type, Primary Key> , layer for Spring Data & QueryDSL.
for example :

```java
@Repository
public interface PetDao extends AbstractDAO<Pet, Integer> {
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
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetDTO extends AbstractDto<Integer> {
	private String name;
	private int age;
	private boolean male;
	private List<String> friends; // friends names
}
```
And a PetWriteDTO with different / less data, for create and update operations :

```java
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetWriteDTO extends AbstractDto<Integer> {
	private String name;
	private int age;
	private boolean male;
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
		List<String> friends = model.getFriends().stream().map(Pet::getName).collect(Collectors.toList());
		
		PetDTO dto = new PetDTO();
		dto.setId(model.getId());
		dto.setName(model.getName());
		dto.setAge(model.getAge());
		dto.setMale(model.isMale());
		dto.setFriends(friends);
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
		// age
		if(dto.getAge() > 0) {
			model.setAge(dto.getAge());
		}
		// sex
		model.setMale(dto.isMale());
		// check friends
		if(!CollectionUtils.isEmpty(dto.getFriendsIds())) {
			for(Integer friendId : dto.getFriendsIds()){
				// pet friend should exists !
				if(petDao.existsById(friendId)){
					model.addFriend(petDao.findById(friendId).get());
				}
			}
		}
	}
}
```

### Controller layer
All you have to do is to extend the abstract Controller class.

for example :

```java
@RestController
@RequestMapping("/pets")
@Api
public class PetController extends AbstractRestController<Pet, Integer, PetDTO, PetWriteDTO> {
	@Override
	@GetMapping
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

Try it !!!

## Database console

http://localhost:8080/h2-console

## Examples

All pets sorted by name
http://localhost:8080/pets?sort=name

Pets with name = vanille
http://localhost:8080/pets?name=vanille

Pets with age = 10
http://localhost:8080/pets?age=10

Pets with age = 8 OR age = 10
http://localhost:8080/pets?age=8&age=10

http://localhost:8080/pets?age=8&male=true

## Known Issues and Limitations

- QueryDSL Predicate does not implement Generics

Due to QueryDSL current limitations and type erasure, you have to override the "findAll" operation in all REST Controllers,
in order to give "QuerydslPredicate" the entity type. I hope it will be fixed in a near future.

- QueryDSL Predicate default binding is not complete

As explained in Spring Data documentation, the default binding is as follows:
 - Object on simple properties as eq.
 - Object on collection like properties as contains.
 - Collection on simple properties as in.

https://docs.spring.io/spring-data/commons/docs/current/reference/html/#core.web.type-safe

I would like to add more bindings, for example :
 - Like
 - Contains
 - Greater Than
 - Lower Than

## What's next ?

- QueryDSL Filters

Current query dsl filters are directly applied from the rest service to the Entity, using entity fields names.
In the next release, filters will be applied using the "Read DTO" fields names.
For example, if a Pet has a "boolean male" attribute, and the PetDTO has a "boolean sex" attribute, user will have to filter with the "sex" name, not with "male". User must not know the database model.


