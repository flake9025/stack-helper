package fr.vvlabs.stackhelper.mapper;

import java.util.ArrayList;
import java.util.List;

import fr.vvlabs.stackhelper.dto.PetDTO;
import fr.vvlabs.stackhelper.model.Pet;

public class PetMapperImpl implements AbstractMapper<Pet, Integer, PetDTO> {

	@Override
	public PetDTO mapToDto(Pet model){
		PetDTO dto = new PetDTO();
		dto.setName(model.getName());
		List<PetDTO> friends = new ArrayList<PetDTO>();
		for(Pet pet : model.getFriends()){
			friends.add(mapToDto(pet));
		}
		dto.setFriends(friends);
		return dto;
	}
}
