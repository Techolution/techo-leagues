package com.makeurpicks.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;

//@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(MockitoJUnitRunner.class)
//@SpringApplicationConfiguration(classes = LeagueApplication.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class LeagueServiceTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private LeagueService leagueService;

	private LeagueRepository leagueRepository;
	private PlayerLeagueRepository playerLeagueRepository;
	private League league = null;
	//@Mock
	//private PlayerLeague playerLeagueMock;

	@Before
	public void setup() {
		leagueRepository = Mockito.mock(LeagueRepository.class);
		playerLeagueRepository = Mockito.mock(PlayerLeagueRepository.class);
		leagueService = new LeagueService(leagueRepository, playerLeagueRepository);

		league = new League();
		league.setId("1001");
		league.setLeagueName("Indian Cricket League");
		league.setPassword("password");
		league.setSeasonId("101");
		league.setAdminId("11");
		league.setActive(true);
	}

	@Test
	public void createLeague_created_success() throws Exception {

		league = leagueService.createLeague(league);
		assertEquals("Indian Cricket League", league.getLeagueName());
		assertNotNull(league.getId());
		
		verify(leagueRepository).save(any(League.class));
	}

	@Test
	public void updateLeague_updated_success() throws Exception {

		when(leagueRepository.findOne(league.getId().toString())).thenReturn(league);
		league = leagueService.updateLeague(league);
		assertEquals("Indian Cricket League", league.getLeagueName());
		
		verify(leagueRepository).save(any(League.class));
	}

	@Test
	public void updateLeague_updated_Fail() throws Exception {

		when(leagueRepository.findOne(league.getId().toString())).thenReturn(null);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage("LEAGUE_NOT_FOUND");
		league = leagueService.updateLeague(league);
		
		//verify(leagueRepository).save(any(League.class));
	}

	@Test
	public void getLeaguesForPlayer_getLeagues_success() {
		String playerId = "10001";

		List<String> leagueIds = new ArrayList<String>(Arrays.asList("1001", "102"));
		List<League> leagues = new ArrayList<>();
		leagues.add(league);
		when(playerLeagueRepository.findIdLeagueIdsByIdPlayerId(playerId)).thenReturn(leagueIds);
		when(leagueRepository.findAll(leagueIds)).thenReturn(leagues);

		Set<LeagueName> leagueNames = leagueService.getLeaguesForPlayer(playerId);
		LeagueName leagueName = leagueNames.iterator().next();

		assertEquals("1001", leagueName.getLeagueId());
		assertEquals("Indian Cricket League", leagueName.getLeagueName());
		assertEquals("101", leagueName.getSeasonId());
	}

	@Test
	public void getLeaguesForPlayer_leagueIds_Null() {
		String playerId = "10001";
		List<String> leagueIds = null;
		Set<LeagueName> expectedResult = new HashSet<LeagueName>();
		when(playerLeagueRepository.findIdLeagueIdsByIdPlayerId(playerId)).thenReturn(leagueIds);
		Set<LeagueName> leagueNames = leagueService.getLeaguesForPlayer(playerId);
		assertEquals(expectedResult.isEmpty(), leagueNames.isEmpty());
	}

	@Test
	public void getPlayersInLeague_getPlayers_success() {
		String leagueId = "1001";
		List<String> players = new ArrayList<String>();
		players.add("Dharmendra Pandit");
		when(playerLeagueRepository.findIdPlayerIdsByIdLeagueId(leagueId)).thenReturn(players);
		Set<String> playerList = leagueService.getPlayersInLeague(leagueId);
		assertEquals("Dharmendra Pandit", playerList.iterator().next());
	}

	@Test
	public void joinLeague_joining_success() {
		String leagueId = "1001";
		String playerId = "1001";

		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId().toString(), playerId));
		//playerLeague.setLeagueId(league.getId().toString());
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		playerLeague.setPlayerId(playerId);

		when(leagueRepository.findOne(leagueId)).thenReturn(league);
		//when(playerLeagueRepository.save(any(PlayerLeague.class))).thenReturn(playerLeague);
		when(playerLeagueRepository.save(playerLeague)).thenReturn(playerLeague);
		leagueService.joinLeague(playerLeague);

		verify(leagueRepository).findOne(leagueId);
		//verify(playerLeagueRepository, Mockito.times(1)).save(any(PlayerLeague.class));
		verify(playerLeagueRepository).save(any(PlayerLeague.class));
	}

	@Test
	public void joinLeague_leagueIdAndNameNull_Fail() {
		String playerId = "1001";

		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId().toString(), playerId));
		playerLeague.setLeagueId(null);
		playerLeague.setLeagueName(null);
		playerLeague.setPassword(league.getPassword());
		playerLeague.setPlayerId(playerId);

		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage("LEAGUE_NOT_FOUND");

		leagueService.joinLeague(playerLeague);
	}

	@Test
	public void joinLeague_leagueIdNull_success() {
		String playerId = "1001";

		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId().toString(), playerId));
		playerLeague.setLeagueId(null);
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		// playerLeague.setPlayerId(playerId);

		// league.setId(1001L);
		when(leagueRepository.findByLeagueName(league.getLeagueName())).thenReturn(league);
		when(leagueRepository.findOne(league.getId().toString())).thenReturn(league);
		when(playerLeagueRepository.save(playerLeague)).thenReturn(playerLeague);
		// expectedException.expect(LeagueValidationException.class);
		// expectedException.expectMessage("LEAGUE_NOT_FOUND");

		leagueService.joinLeague(playerLeague);

		verify(leagueRepository).findByLeagueName(league.getLeagueName());
		verify(leagueRepository).findOne(league.getId().toString());
		verify(playerLeagueRepository).save(any(PlayerLeague.class));
	}

	@Test
	public void joinLeague_leagueIdAndLeagueObjectNull_fail() {
		String playerId = "1001";
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId().toString(), playerId));
		playerLeague.setLeagueId(null);
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		playerLeague.setPlayerId(playerId);
		// league.setId(101L);
		when(leagueRepository.findByLeagueName(league.getLeagueName())).thenReturn(null);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage("LEAGUE_NOT_FOUND");
		leagueService.joinLeague(playerLeague);
		verify(leagueRepository).findByLeagueName(league.getLeagueName());
	}

	@Test
	public void getLeagueByName_getLeague_success() {
		String leagueName = "Indian Cricket League";
		when(leagueRepository.findByLeagueName(leagueName)).thenReturn(league);
		League expectedResult = leagueService.getLeagueByName(leagueName);
		assertEquals(leagueName, expectedResult.getLeagueName());
		assertNotNull(expectedResult.getId());
		verify(leagueRepository).findByLeagueName(leagueName);
	}

	@Test
	public void removePlayerFromLeague_remove_success() {
		String leagueId = "1001";
		String playerId = "1001";
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId().toString(), playerId));
		// playerLeague.setLeagueId(leagueId);
		// playerLeague.setLeagueName(league.getLeagueName());
		// playerLeague.setPassword(league.getPassword());
		// playerLeague.setPlayerId(playerId);
		when(leagueRepository.findOne(leagueId)).thenReturn(league);
		when(playerLeagueRepository.findByIdLeagueIdAndIdPlayerId(league.getId().toString(), playerId))
				.thenReturn(playerLeague);
		leagueService.removePlayerFromLeague(leagueId, playerId);
		verify(playerLeagueRepository).delete(playerLeague);
	}

	@Test
	public void removePlayerFromLeague_remove_fail() {
		String leagueId = "101";
		String playerId = "1001";
		League league = null;
		when(leagueRepository.findOne(leagueId)).thenReturn(league);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage("LEAGUE_NOT_FOUND");
		leagueService.removePlayerFromLeague(leagueId, playerId);
	}

	@Test
	public void validateLeague_success() {
		leagueService.validateLeague(league);
	}

	@Test
	public void getAllLeagues_success() {
		List<League> leagueList = new ArrayList<>();
		leagueList.add(league);
		when(leagueRepository.findAll()).thenReturn(leagueList);
		Iterable<League> leagues = leagueService.getAllLeagues();
		League actualResult = leagues.iterator().next();
		assertEquals(league.getLeagueName(), actualResult.getLeagueName());
	}

	@Test
	public void deleteLeague_success() {
		String leagueId = "1001";
		String playerId = "11";
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId().toString(), playerId));

		List<String> playerIds = new ArrayList<>();
		playerIds.add(playerId);

		when(playerLeagueRepository.findIdPlayerIdsByIdLeagueId(leagueId)).thenReturn(playerIds);
		when(leagueRepository.findOne(leagueId)).thenReturn(league);
		when(playerLeagueRepository.findByIdLeagueIdAndIdPlayerId(league.getId().toString(), playerId))
				.thenReturn(playerLeague);

		leagueService.deleteLeague(leagueId);

		verify(playerLeagueRepository).delete(playerLeague);
		verify(leagueRepository).delete(leagueId);
	}

	@Test
	public void getLeagueBySeasonId_success() {
		String seasonId = "101";
		List<League> leagueList = new ArrayList<>();
		leagueList.add(league);

		when(leagueRepository.findLeagueBySeasonId(seasonId)).thenReturn(leagueList);

		List<League> actualLeagueList = leagueService.getLeagueBySeasonId(seasonId);
		League actualLeague = actualLeagueList.get(0);

		assertEquals(league.getLeagueName(), actualLeague.getLeagueName());
		assertNotNull(actualLeague.getSeasonId());
	}
}
