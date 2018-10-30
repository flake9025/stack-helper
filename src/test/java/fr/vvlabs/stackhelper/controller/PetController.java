package fr.vvlabs.stackhelper.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.vvlabs.stackhelper.dto.PetDTO;
import fr.vvlabs.stackhelper.dto.PetWriteDTO;
import fr.vvlabs.stackhelper.model.Pet;

@RestController
@RequestMapping("/pets")
//@Api
public class PetController extends AbstractRestController<Pet, Integer, PetDTO, PetWriteDTO> {
	// crud endpoints are already defined !
	// add only new services
}