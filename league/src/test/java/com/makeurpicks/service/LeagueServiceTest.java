package com.makeurpicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.LeagueValidationException.LeagueExceptions;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;

@RunWith(MockitoJUnitRunner.class)
public class LeagueServiceTest {

	@Mock
	public LeagueRepository leagueRepositoryMock;

	@Mock
	public PlayerLeagueRepository playerLeagueRepositoryMock;

	@InjectMocks
	public LeagueService leagueService;

	@Rule
	public ExpectedException expectedExp = ExpectedException.none();

	private League generateLeague() {
		League league = new League();
		league.setId(UUID.randomUUID().toString());
		league.setLeagueName("aa");
		league.setSeasonId("aa");
		league.setAdminId("aaa");
		league.setPassword("aa");
		return league;
	}

	private PlayerLeague generatePlayerLeague() {
		PlayerLeagueId playerLeagueId = new PlayerLeagueId("leagueId", "playerId");
		PlayerLeague playerLeague = new PlayerLeague(playerLeagueId);
		return playerLeague;
	}

	@Test
	public void validateLeagueTest_LeagueNameNull() {
		expectedExp.expect(LeagueValidationException.class);
		League league = new League();
		league.setLeagueName("");
		leagueService.validateLeague(league);
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_NULL.toString());
	}

	@Test
	public void validateLeagueTest_LeagueNameInUse() {
		expectedExp.expect(LeagueValidationException.class);
		League league = new League();
		league.setLeagueName("a");
		when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(new League());
		leagueService.validateLeague(league);
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NAME_IN_USE.toString());
	}

	@Test
	public void validateLeagueTest_SeasonIdNull() {
		expectedExp.expect(LeagueValidationException.class);
		League league = new League();
		league.setLeagueName("a");
		league.setSeasonId(null);
		when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(null);
		leagueService.validateLeague(league);
		expectedExp.expectMessage(LeagueExceptions.SEASON_ID_IS_NULL.toString());
	}

	@Test
	public void validateLeagueTest_AdminIdNull() {
		expectedExp.expect(LeagueValidationException.class);
		League league = new League();
		league.setLeagueName("a");
		league.setSeasonId("a");
		league.setAdminId(null);
		when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(null);
		leagueService.validateLeague(league);
		expectedExp.expectMessage(LeagueExceptions.ADMIN_NOT_FOUND.toString());
	}

	@Test
	public void createLeagueTest_leagueNull() {
		expectedExp.expect(NullPointerException.class);
		leagueService.createLeague(null);
	}

	@Test
	public void createLeagueTest_LeagueNameNull() {
		expectedExp.expect(LeagueValidationException.class);
		League league = new League();
		league.setId(UUID.randomUUID().toString());
		leagueService.createLeague(league);
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_NULL.toString());
	}

	@Test
	public void createLeagueTest_SeasonIdNull() {
		expectedExp.expect(LeagueValidationException.class);
		League league = new League();
		league.setId(UUID.randomUUID().toString());
		league.setLeagueName("aa");
		leagueService.createLeague(league);
		expectedExp.expectMessage(LeagueExceptions.SEASON_ID_IS_NULL.toString());
	}

	@Test
	public void createLeagueTest_AdminIdNull() {
		expectedExp.expect(LeagueValidationException.class);
		League league = new League();
		league.setId(UUID.randomUUID().toString());
		league.setLeagueName("aa");
		league.setSeasonId("aa");
		league.setPassword("aa");
		leagueService.createLeague(league);
		expectedExp.expectMessage(LeagueExceptions.ADMIN_NOT_FOUND.toString());
	}

	@Test
	public void updateLeagueTest_leagueNull() {
		expectedExp.expect(NullPointerException.class);
		leagueService.updateLeague(null);
	}

	@Test
	public void updateLeagueTest_LeagueNotFound() {
		expectedExp.expect(LeagueValidationException.class);
		League league = generateLeague();
		when(leagueRepositoryMock.findOne(anyString())).thenReturn(null);
		leagueService.updateLeague(league);
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
	}

	@Test
	public void updateLeagueTest_Save() {
		League league = generateLeague();
		when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
		leagueService.updateLeague(league);
		verify(leagueRepositoryMock).save(league);
	}

	@Test
	public void getLeaguesForPlayerTest_PlayerIdNull() {
		Set<LeagueName> ret = leagueService.getLeaguesForPlayer(null);
		assertThat(ret.size() == 0);
	}

	@Test
	public void getLeaguesForPlayerTest_PlayerIdCorrect() {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(anyString())).thenReturn(list);
		leagueService.getLeaguesForPlayer("anyStri");
		verify(leagueRepositoryMock).findAll(list);
	}

	@Test
	public void getPlayersInLeagueTest_LeagueIdInvalid() {
		Set<String> players = leagueService.getPlayersInLeague("invalidleagueid");
		assertThat(players.size() == 0);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void joinLeagueTest_InvalidPassword() {
		expectedExp.expect(LeagueValidationException.class);
		PlayerLeague playerLeague = generatePlayerLeague();
		League league = generateLeague();
		when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
		when(playerLeagueRepositoryMock.save(anyList())).thenReturn(anyObject());
		leagueService.joinLeague(playerLeague);
		expectedExp.expectMessage(LeagueExceptions.INVALID_LEAGUE_PASSWORD.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void joinLeagueTest_LeagueIdLeagueNameNull() {
		expectedExp.expect(LeagueValidationException.class);
		PlayerLeague playerLeague = generatePlayerLeague();
		League league = generateLeague();
		league.setId(null);
		league.setLeagueName(null);
		when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
		when(playerLeagueRepositoryMock.save(anyList())).thenReturn(anyObject());
		leagueService.joinLeague(playerLeague);
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
	}

	@Test
	public void joinLeagueTest_LeagueIdNull() {
		expectedExp.expect(LeagueValidationException.class);
		PlayerLeague playerLeague = generatePlayerLeague();
		League league = generateLeague();
		league.setId(null);
		when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(anyObject());
		leagueService.joinLeague(playerLeague);
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
	}

	@Test
	public void joinLeagueTest_NullLeague() {
		expectedExp.expect(LeagueValidationException.class);
		League league = generateLeague();
		when(leagueRepositoryMock.findOne(anyString())).thenReturn(null);
		leagueService.joinLeague(league.getId(), "PlayerId", league.getPassword());
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void joinLeagueTest_Save() {
		League league = generateLeague();
		when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
		when(playerLeagueRepositoryMock.save(anyList())).thenReturn(anyObject());
		leagueService.joinLeague(league.getId(), "PlayerId", league.getPassword());
	}

	@Test
	public void getLeagueByIdTest_IncorrectId() {
		assertNull(leagueService.getLeagueById("x"));
	}

	@Test
	public void getLeagueByNameTest_IncorrectName() {
		assertNull(leagueService.getLeagueByName("x"));
	}

	@Test
	public void removePlayerFromLeagueTest_InvalidLeague() {
		expectedExp.expect(LeagueValidationException.class);
		when(leagueService.getLeagueById(null)).thenReturn(null);
		leagueService.removePlayerFromLeague(null, null);
		expectedExp.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
	}

	@Test
	public void removePlayerFromLeagueTest_ValidLeague() {
		League league = generateLeague();
		PlayerLeagueId playerLeagueId = new PlayerLeagueId("leagueId", "playerId");
		PlayerLeague playerLeague = new PlayerLeague(playerLeagueId);

		when(leagueService.getLeagueById(anyObject())).thenReturn(league);
		when(playerLeagueRepositoryMock.findByIdLeagueIdAndIdPlayerId(anyString(), anyString()))
				.thenReturn(playerLeague);
		leagueService.removePlayerFromLeague(league.getId(), "playerId");
		verify(playerLeagueRepositoryMock).delete(playerLeague);
	}

	@Test
	public void getAllLeaguesTest() {
		Iterable<League> list = leagueService.getAllLeagues();
		assertNotNull(list);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteLeagueTest_InvalidLeagueId() {
		when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(anyString())).thenReturn(anyList());
		when(leagueService.getLeagueById(null)).thenReturn(null);
		leagueService.deleteLeague("x");
		verify(leagueRepositoryMock).delete("x");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteLeagueTest_ValidLeagueId() {
		League league = generateLeague();
		when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(anyString())).thenReturn(anyList());
		when(leagueService.getLeagueById("a")).thenReturn(league);
		leagueService.deleteLeague("a");
		verify(leagueRepositoryMock).delete("a");
	}

	@Test
	public void getLeagueBySeasonIdTest_InvalidSeasonId() {
		when(leagueRepositoryMock.findLeagueBySeasonId(null)).thenReturn(null);
		assertNull(leagueService.getLeagueBySeasonId(null));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getLeagueBySeasonIdTest_ValidSeasonId() {
		when(leagueRepositoryMock.findLeagueBySeasonId(anyString())).thenReturn(anyList());
		assertNotNull(leagueService.getLeagueBySeasonId("a"));
	}

}
