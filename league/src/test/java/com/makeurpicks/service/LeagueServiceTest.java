package com.makeurpicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import com.makeurpicks.LeagueApplication;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.LeagueValidationException.LeagueExceptions;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = LeagueApplication.class)
	public class LeagueServiceTest {
	@Mock
	public LeagueRepository leagueRepositoryMock;
	@Mock
	public PlayerLeagueRepository playerLeagueRepositoryMock;
	
	@Autowired 
	@InjectMocks
	public LeagueService leagueService;

	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@AfterClass
	public static void close() {
	}
	
	@Test
	public void validateLeague_leagueNameIsNull_throwsLeagueValidationException() {
		expectedEx.expect(LeagueValidationException.class);
		expectedEx.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_NULL.toString());
		
		League league = new LeagueBuilder(UUID.randomUUID().toString()).build();
		
		leagueService.validateLeague(league);
	}
	
	@Test
	public void validateLeague_leagueNameAlreadyExists_throwsLeagueValidationException() {
		expectedEx.expect(LeagueValidationException.class);
		expectedEx.expectMessage(LeagueExceptions.LEAGUE_NAME_IN_USE.toString());
		
		League league = new LeagueBuilder(UUID.randomUUID().toString()).withName("karthik").build();
		
		when(leagueService.getLeagueByName(league.getLeagueName())).thenReturn(league);
		leagueService.validateLeague(league);
	}
	
	@Test
	public void validateLeague_seasonIdIsNull_throwsLeagueValidationException() {
		expectedEx.expect(LeagueValidationException.class);
		expectedEx.expectMessage(LeagueExceptions.SEASON_ID_IS_NULL.toString());
		
		League league = new LeagueBuilder(UUID.randomUUID().toString()).withName("karthik").build();
		
		when(leagueService.getLeagueBySeasonId(league.getSeasonId())).thenReturn(null);
		leagueService.validateLeague(league);
	}
	
	@Test
	public void validateLeague_adminIdIsNull_throwsLeagueValidationException() {
		expectedEx.expect(LeagueValidationException.class);
		expectedEx.expectMessage(LeagueExceptions.ADMIN_NOT_FOUND.toString());
		
		League league = new LeagueBuilder(UUID.randomUUID().toString()).withName("karthik").withSeasonId(UUID.randomUUID().toString()).build();
		
		when(leagueService.getLeagueById(league.getId())).thenReturn(null);
		leagueService.validateLeague(league);
	}
	 
	@Test
	public void validateLeague_isValidPlayer_throwsLeagueValidationException() {
		expectedEx.expect(LeagueValidationException.class);
		expectedEx.expectMessage(LeagueExceptions.ADMIN_NOT_FOUND.toString());
		
		League league = new LeagueBuilder(UUID.randomUUID().toString()).withName("karthik").withSeasonId(UUID.randomUUID().toString()).build();
		
		when(leagueService.getLeagueById(league.getId())).thenReturn(null);
		leagueService.validateLeague(league);
	}
	
	@Test
	public void createLeague_withLeague_shouldSaveOnRepository() {
		League league = new LeagueBuilder(UUID.randomUUID().toString()).withAdminId(UUID.randomUUID().toString())
				.withName("pickem").withPassword(UUID.randomUUID().toString()).withSeasonId(UUID.randomUUID().toString()).withNoSpreads()
				.build();
		leagueService.createLeague(league);
		verify(leagueRepositoryMock).save(league);
	}
	
//	@Test
//	public void createLeagueShouldAddPlayerToLeagueAndSaveOnRepository(){
//		
//		String leagueId = UUID.randomUUID().toString();
//		String adminId = UUID.randomUUID().toString();
//		League league = new LeagueBuilder(leagueId).withAdminId(adminId)
//				.withName("pickem").withPassword(UUID.randomUUID().toString()).withSeasonId(UUID.randomUUID().toString()).withNoSpreads()
//				.build();
//		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(leagueId,league.getAdminId()));
//		playerLeague.setLeagueId(leagueId);
//		playerLeague.setLeagueName(league.getLeagueName());
//		playerLeague.setPassword(league.getPassword());
//		playerLeague.setPlayerId(adminId);
//		when(leagueService.addPlayerToLeague(league,league.getAdminId())).thenReturn(playerLeague);
//		verify(playerLeagueRepositoryMock).save(playerLeague);
//	}

    @Test
	public void updateLeague_WithNoLeague_throwsLeagueValidationException(){
		
		expectedEx.expect(LeagueValidationException.class);
		League league = new LeagueBuilder(UUID.randomUUID().toString())
				.withAdminId(UUID.randomUUID().toString())
				.withName(UUID.randomUUID().toString())
				.withPassword(UUID.randomUUID().toString())
				.withSeasonId(UUID.randomUUID().toString()).withNoSpreads()
				.build();
		when(leagueRepositoryMock.findOne(league.getId())).thenReturn(null);
		leagueService.updateLeague(league);
		
	}
	
	@Test
	public void updateLeague_withLeague_shouldSaveOnRepository(){
		League league = new LeagueBuilder(UUID.randomUUID().toString())
				.withAdminId(UUID.randomUUID().toString())
				.withName(UUID.randomUUID().toString())
				.withPassword(UUID.randomUUID().toString())
				.withSeasonId(UUID.randomUUID().toString()).withNoSpreads()
				.build();
		when(leagueRepositoryMock.findOne(league.getId())).thenReturn(league);
		leagueService.updateLeague(league);
		verify(leagueRepositoryMock).save(league);
		
	}
	
	@Test
	public void removePlayerFromLeague_withNoLeague_throwsLeagueValidationException(){
		expectedEx.expect(LeagueValidationException.class);
		expectedEx.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		League league = new LeagueBuilder(UUID.randomUUID().toString())
				.withAdminId(UUID.randomUUID().toString())
				.withName(UUID.randomUUID().toString())
				.withPassword(UUID.randomUUID().toString())
				.withSeasonId(UUID.randomUUID().toString()).withNoSpreads()
				.build();
		when(leagueRepositoryMock.findOne(league.getId())).thenReturn(null);
        leagueService.removePlayerFromLeague(league.getId(), UUID.randomUUID().toString());
	}
	
	@Test
	public void removePlayerFromLeague_withLeague_shouldDeleteOnRepository(){
		League league = new LeagueBuilder(UUID.randomUUID().toString())
				.withAdminId(UUID.randomUUID().toString())
				.withName(UUID.randomUUID().toString())
				.withPassword(UUID.randomUUID().toString())
				.withSeasonId(UUID.randomUUID().toString()).withNoSpreads()
				.build();
		String playerId = UUID.randomUUID().toString();
		when(leagueRepositoryMock.findOne(league.getId())).thenReturn(league);
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId(),league.getAdminId()));
		playerLeague.setLeagueId(league.getId());
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		playerLeague.setPlayerId(playerId);
		when(playerLeagueRepositoryMock.findByIdLeagueIdAndIdPlayerId(league.getId(), playerId)).thenReturn(playerLeague);
        leagueService.removePlayerFromLeague(league.getId(), playerId);
        verify(playerLeagueRepositoryMock).delete(playerLeague);
	}
	
	
	@Test
	public void getLeaguesForPlayer_withNoLeagues_throwLeagueValidationException(){
		String playerId = UUID.randomUUID().toString();
		when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(playerId)).thenReturn(null);
		Set<LeagueName> set = new  HashSet<LeagueName>();
        set = leagueService.getLeaguesForPlayer(playerId);
		assertTrue(set.size() == 0);
	}
	
	
	@Test
	public void joinLeague_withNoPlayerLeague_throwsLeagueValidationException(){
		expectedEx.expect(LeagueValidationException.class);
		expectedEx.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(null,null));
		when(playerLeagueRepositoryMock.findOne(playerLeague.getLeagueId())).thenReturn(null);
		when(playerLeagueRepositoryMock.findOne(playerLeague.getLeagueName())).thenReturn(null);
	    leagueService.joinLeague(playerLeague);
	}
	
	
	
		
}
