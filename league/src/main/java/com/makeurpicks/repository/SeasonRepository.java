package com.makeurpicks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makeurpicks.domain.Season;

@Repository
public interface SeasonRepository extends JpaRepository<Season, String> {//CrudRepository<Season, String>{

	public List<Season> getSeasonsByLeagueType(String leaueType);
	public Season save(Season season);
	public void delete(String seasonId);
	
}
