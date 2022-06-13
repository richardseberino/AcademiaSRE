package com.gm4c.tef.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gm4c.tef.dto.TefDto;


@Repository
public interface TefRepository extends CrudRepository<TefDto, String> 
{

	
}
