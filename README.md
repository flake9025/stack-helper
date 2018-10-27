# Multi Tier Stack Builder Helper

- Master (1.0.0)

## Overview
This is the stack builder helper project, which allows easy implementation of CRUD REST Services.
Using generics types for required objects :
- Entity
- Primary Key
- DTO used for read operations
- DTO used for create operations (can be the same)

It will produce a Web Controller and a Service Layer, with the following operations :
- create
- countAll
- findAll
- findById
- update
- deleteById
- deleteByIdList

### Prerequisites
- Spring MVC (for REST Controllers)
- Spring 5 (for IOC and Beans)
- Spring Data 2 (for Persistence)

## Getting Started

### Entities
If you already have Spring Data Persistable entities, this is already fine.
Otherwise, you can choose to add "implements Persistable<K>" where K is your primary key type.
Or, you can choose to extend any helper of this project :
	- AbstractModel class : generic entity
	- AbstractModelGeneratedId class  : generic entity with generated Id
	
for example :

```java
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class Pet extends AbstractModelGenereatedId<Integer>{

    private String name;
    
}
```

### DTO
All you have to do is to extend the Abstract Dto class.
You can choose to use the same DTO for both Read and Write operations, or use a different one for each.
for example :

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetDTO extends AbstractDto<Integer> {

	private static final long serialVersionUID = -6010374309568122948L;

	private String name;
}
```

### Mappers
You will need mappers to walk from an Entity to a DTO, and vice versa.
All you have to do is to create an interface and extend the abstract Mapper class
for example :

```java
@Mapper(componentModel="spring")
public interface PetMapper extends AbstractMapper<Pet, Integer, PetDTO, PetDTO> {

	@Override
	public PetDTO PetDTO(Pet model);

	@Override
	public Pet mapToModel(PetDTO writeDto);
}
```

### Service Layer
All you have to do is to extend the abstract Service class, and fill the "updateModel" method with business logic.
for example :

```java
@Service
public class PetService extends AbstractService<Pet, Integer, PetDTO, PetDTO> {
	@Override
	protected Pet updateModel(final Pet model, final PetDTO dto) {
		// some business logic :
		if(!dto.getName().isEmpty() {
			model.setName(dto.getName());
		}
		return model;
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
public class PetController extends AbstractRestController<Pet, Integer, PetDTO, PetDTO> {
	// crud endpoints are already defined !
	// add only new services
}
```
