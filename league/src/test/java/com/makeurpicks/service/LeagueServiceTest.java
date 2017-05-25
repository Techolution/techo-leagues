package com.makeurpicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.LeagueValidationException.LeagueExceptions;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;

/**
 * @author tarun
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LeagueServiceTest {

	@Mock
	private LeagueRepository leagueRepository;

	@Mock
	private PlayerLeagueRepository playerLeagueRepository;

	@InjectMocks
	private LeagueService  leagueService = new LeagueService(leagueRepository, playerLeagueRepository);

	private League league;
	List<League> leagues = new ArrayList<League>();

	private void stubData(){

		String player1Id = "1";

		league = new LeagueBuilder()
				.withAdminId(player1Id)
				.withName("pickem")
				.withPassword("football")
				.withSeasonId("1")
				.build();
		leagues.add(league);

	}

	@Before
	public void setup() {
		stubData();
	}

	@Test
	public void testCreateLeague(){
		assertNotNull(leagueService.createLeague(league));
	}

	@Test(expected = LeagueValidationException.class)
	public void testCreateLeagueWithoutName(){
		league.setLeagueName("");
		leagueService.createLeague(league);
	}
	@Test
	public void testCreateLeagueWithoutNameExceptionMessage(){
		try{
			league.setLeagueName("");
			leagueService.createLeague(league);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.LEAGUE_NAME_IS_NULL));
		}

	}

	@Test
	public void testCreateLeagueWithNameAlreadyUSed(){
		try{
			when(leagueRepository.findByLeagueName(anyString())).thenReturn(league);
			leagueService.createLeague(league);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.LEAGUE_NAME_IN_USE));
		}
	}

	@Test
	public void testCreateLeagueWithoutSeasonId(){
		try{
			league.setSeasonId("");
			leagueService.createLeague(league);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.SEASON_ID_IS_NULL));
		}
	}

	@Test
	public void testCreateLeagueWithoutAdminID(){
		try{
			league.setAdminId("");
			leagueService.createLeague(league);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.ADMIN_NOT_FOUND));
		}
	}

	@Test
	public void testUpdateLeague(){
		when(leagueRepository.findOne(anyString())).thenReturn(league);
		assertNotNull(leagueService.updateLeague(league));
	}

	@Test
	public void testUpdateLeagueNoLeagueFound(){
		try{
			leagueService.updateLeague(league);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.LEAGUE_NOT_FOUND));
		}
	}

	@Test
	public void testGetPlayersInLeague() {
		assertNotNull(leagueService.getPlayersInLeague("leagueId"));
	}

	@Test
	public void testGetLeaguesForPlayerWithnoList()  {
		assertNotNull(leagueService.getLeaguesForPlayer("playerId"));
		assertThat(leagueService.getLeaguesForPlayer("playerId").isEmpty());
	}

	@Test
	public void testGetLeaguesForPlayer()  {
		List<String> leagueIds = new ArrayList<String>();
		leagueIds.add(league.getId());
		when(playerLeagueRepository.findIdLeagueIdsByIdPlayerId(anyString())).thenReturn(leagueIds);
		when(leagueRepository.findAll(leagueIds)).thenReturn(leagues);

		assertNotNull(leagueService.getLeaguesForPlayer("playerId"));
		assertThat(!leagueService.getLeaguesForPlayer("playerId").isEmpty());
	}

	@Test
	public void testJoinLeagueLeagueNotFound(){
		try{
			PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId());
			leagueService.joinLeague(playerLeague);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.LEAGUE_NOT_FOUND));
		}
	}

	@Test
	public void testJoinLeagueLeagueIsNull(){
		try{
			PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId("leagueId","playerId"));
			leagueService.joinLeague(playerLeague);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.LEAGUE_NOT_FOUND));
		}
	}

	@Test
	public void testJoinLeagueLeagueIdisNull(){
		try{
			PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(null,"playerId"));
			playerLeague.setLeagueName(league.getLeagueName());
			leagueService.joinLeague(playerLeague);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.LEAGUE_NOT_FOUND));
		}
	}

	@Test
	public void testJoinLeagueLeagueInvalidPassword(){
		try{
			when(leagueRepository.findByLeagueName(anyString())).thenReturn(league);
			when(leagueRepository.findOne(anyString())).thenReturn(league);
			PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(null,"playerId"));
			playerLeague.setLeagueName(league.getLeagueName());
			playerLeague.setPassword("randomPassword");
			leagueService.joinLeague(playerLeague);
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.INVALID_LEAGUE_PASSWORD));
		}
	}

	@Test
	public void testJoinLeagueLeague(){
		when(leagueRepository.findOne(anyString())).thenReturn(league);
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId("leagueId","playerId"));
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		leagueService.joinLeague(playerLeague);
	}

	@Test
	public void testRemovePlayerFromLeagueNoLeagueFound(){
		try{
			PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(null,"playerId"));
			playerLeague.setLeagueName(league.getLeagueName());
			leagueService.removePlayerFromLeague("leagueId", "playerId");
		} catch (LeagueValidationException e){
			assertThat(e.getMessage().equals(LeagueExceptions.LEAGUE_NOT_FOUND));
		}
	} 

	@Test
	public void testRemovePlayerFromLeague(){
		when(leagueRepository.findOne(anyString())).thenReturn(league);
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(null,"playerId"));
		playerLeague.setLeagueName(league.getLeagueName());
		when(playerLeagueRepository.findByIdLeagueIdAndIdPlayerId(anyString(),anyString())).thenReturn(playerLeague);
		leagueService.removePlayerFromLeague("leagueId", "playerId");
	}

	@Test
	public void testDeleteLeague() {
		when(leagueRepository.findOne(anyString())).thenReturn(league);
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(null,"playerId"));
		playerLeague.setLeagueName(league.getLeagueName());
		when(playerLeagueRepository.findByIdLeagueIdAndIdPlayerId(anyString(),anyString())).thenReturn(playerLeague);
		leagueService.deleteLeague("leagueId");
	}

	@Test
	public void testGetAllLeagues() {
		when(leagueRepository.findAll()).thenReturn(leagues);
		assertNotNull(leagueService.getAllLeagues());
	}

	@Test
	public void testGetLeagueBySeasonId() {
		when(leagueRepository.findLeagueBySeasonId(anyString())).thenReturn(leagues);
		assertNotNull(leagueService.getLeagueBySeasonId("seasonId"));
	}
}
