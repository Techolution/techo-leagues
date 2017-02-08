package com.makeurpicks.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.LeagueValidationException.LeagueExceptions;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;

//@RunWith(MockitoJUnitRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LeagueServiceTest {

	@Mock
	public LeagueRepository leagueRepositoryMock;
	
	@Mock
	public PlayerLeagueRepository playerLeagueRepository;
	
	@Autowired
	@InjectMocks
	public LeagueService leagueService;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	// Method - validateLeague(League league)
	// Method - validateLeague(League league) - Positive test cases

	// Method - validateLeague(League league) - Negative test cases

	@Test
	public void validateLeague_emptyLeagueName() {

		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_NULL.toString());

		leagueService.validateLeague(getLeague(null, null));
	}

	@Test
	public void validateLeague_leagueNameInUse() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(LeagueExceptions.LEAGUE_NAME_IN_USE.toString());

		League league = getLeague("pickem", null);

		when(leagueService.getLeagueByName(league.getLeagueName())).thenReturn(league);
		leagueService.validateLeague(league);

	}

	@Test
	public void validateLeague_invalidSeason() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(LeagueExceptions.SEASON_ID_IS_NULL.toString());

		League league = getLeague("pickem", null);

		when(leagueService.getLeagueByName(league.getLeagueName())).thenReturn(null);

		leagueService.validateLeague(league);

	}

	@Test
	public void validateLeague_invalidAdmin() {

		expectedEx.expect(LeagueValidationException.class);

		League league = getLeague("pickem", UUID.randomUUID().toString());

		when(leagueService.getLeagueByName(league.getLeagueName())).thenReturn(null);
		leagueService.validateLeague(league);

	}

	// Method - createLeague(League league)
	// Method - createLeague(League league) - Positive test cases
	@Test
	public void createLeague_saveToRepository() {
		League league = getLeagueFull();
		leagueService.createLeague(league);
		verify(leagueRepositoryMock).save(league);
	}

	@Test
	public void createLeague_addPlayerToLeague() {
		League league = getLeagueFull();
		when(leagueRepositoryMock.findOne(UUID.randomUUID().toString())).thenReturn(league);
		leagueService.createLeague(league);
	}

	// Method - createLeague(League league) - Negative test cases

	// Method - updateLeague(League league)
	// Method - updateLeague(League league) - Positive test cases
	@Test
	public void updateLeague_saveLeagueInRepository() {
		String playerId = UUID.randomUUID().toString();
		String leagueId = UUID.randomUUID().toString();
		String seasonId = UUID.randomUUID().toString();
		League league = new LeagueBuilder(leagueId).withAdminId(playerId).withName("pickem").withPassword("football")
				.withSeasonId(seasonId).withNoSpreads().build();
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(league);
		leagueService.updateLeague(league);
		verify(leagueRepositoryMock).save(league);
	}

	// Method - updateLeague(League league) - Negative test cases
	@Test
	public void updateLeague_leagueDoesnotExist() {
		expectedEx.expect(LeagueValidationException.class);
		League league = getLeagueFull();
		when(leagueRepositoryMock.findOne(UUID.randomUUID().toString())).thenReturn(null);
		leagueService.updateLeague(league);
	}

	// Method - getLeaguesForPlayer(String playerId)
	// Method - getLeaguesForPlayer(String playerId) - Positive test cases

	// Method - getLeaguesForPlayer(String playerId) - Negative test cases
	@Test
	public void getLeaguesForPlayer_leaguesIdsNull() {
		String playerId = UUID.randomUUID().toString();
		when(playerLeagueRepository.findIdLeagueIdsByIdPlayerId(playerId)).thenReturn(null);
		Set<LeagueName> set = leagueService.getLeaguesForPlayer(playerId);
		assertTrue(set.size() == 0);
	}

	// Method - removePlayerFromLeague(String leagueId, String playerId)
	// Method - removePlayerFromLeague(String leagueId, String playerId) -
	// Positive test cases
	@Test
	public void removePlayerFromLeague_callToDelete() {
		String playerId = UUID.randomUUID().toString();
		String leagueId = UUID.randomUUID().toString();

		League league = getLeagueFull();
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId(), playerId));
		playerLeague.setLeagueId(league.getId());
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		playerLeague.setPlayerId(playerId);
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(league);
		when(playerLeagueRepository.findByIdLeagueIdAndIdPlayerId(league.getId(), playerId)).thenReturn(playerLeague);
		leagueService.removePlayerFromLeague(leagueId, playerId);
		verify(playerLeagueRepository).delete(playerLeague);
	}

	// Method - removePlayerFromLeague(String leagueId, String playerId) -
	// Negative test cases
	@Test
	public void removePlayerFromLeague_invalidLeague() {
		expectedEx.expect(LeagueValidationException.class);
		String playerId = UUID.randomUUID().toString();
		String leagueId = UUID.randomUUID().toString();
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(null);
		leagueService.removePlayerFromLeague(leagueId, playerId);
	}

	private League getLeague(String leagueName, String seasonId) {

		League league = null;
		String leagueId = UUID.randomUUID().toString();

		if (null != leagueName && null != seasonId) {
			league = new LeagueBuilder(leagueId).withName(leagueName).withSeasonId(seasonId).build();

		} else if (null != leagueName) {
			league = new LeagueBuilder(leagueId).withName(leagueName).build();

		} else {
			league = new LeagueBuilder(leagueId).build();
		}

		return league;
	}

	private League getLeagueFull() {
		String playerId = UUID.randomUUID().toString();
		String leagueId = UUID.randomUUID().toString();
		String seasonId = UUID.randomUUID().toString();
		League league = new LeagueBuilder(leagueId).withAdminId(playerId).withName("pickem").withPassword("football")
				.withSeasonId(seasonId).withNoSpreads().build();
		return league;
	}

	@AfterClass
	public static void close() {
	}

}
