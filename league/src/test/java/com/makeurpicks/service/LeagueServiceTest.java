package com.makeurpicks.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.springframework.boot.test.context.SpringBootTest;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.LeagueValidationException.LeagueExceptions;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import com.makeurpicks.utils.HelperUtils;

/**
 * 
 * @author Shyam
 *
 */

@RunWith(MockitoJUnitRunner.class) 
@SpringBootTest 
public class LeagueServiceTest {
	
	private static final String EMPTY  				= "";
	private static final String INVALID_LEAGUE_NAME = "###";
	private static final String LEAGUE_NAME 		= "NFL";
	private static final String SEASON_ID			= "SEASON_123";
	private static final String ADMIN_ID			= "ADMIN_123";
	private static final String NULL_STR            = null;

	@Autowired
	@InjectMocks
	private LeagueService leagueService;

	@Mock
	private LeagueRepository leagueRepositoryMock;
	
	@Mock
	private PlayerLeagueRepository playerLeagueRepositoryMock;
	
	@Mock
	private HelperUtils helperUtilsMock;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void validateLeague_LeagueIsNull_LeagueValidationException() {
		leagueService.validateLeague(null);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_IS_NULL.toString());
	}
	
	@Test
	public void validateLeague_LeagueNameNull_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(NULL_STR);
		leagueService.validateLeague(mockLeague);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_NULL.toString());
	}
	
	@Test
	public void validateLeague_LeagueNameEmpty_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(EMPTY);
		leagueService.validateLeague(mockLeague);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_EMPTY.toString());
	}
	
	@Test
	public void validateLeague_LeagueNameInUse_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		when(leagueRepositoryMock.findByLeagueName(LEAGUE_NAME)).thenReturn(mockLeague);
		leagueService.validateLeague(mockLeague);
		verify(leagueRepositoryMock).findByLeagueName(LEAGUE_NAME);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NAME_IN_USE.toString());
	}
	
	@Test
	public void validateLeague_SeasonIdNull_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		mockLeague.setSeasonId(NULL_STR);
		leagueService.validateLeague(mockLeague);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.SEASON_ID_IS_NULL.toString());
	}
	
	@Test
	public void validateLeague_SeasonIdEmpty_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		mockLeague.setSeasonId(EMPTY);
		leagueService.validateLeague(mockLeague);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.SEASON_ID_IS_NULL.toString());
	}
	
	@Test
	public void validateLeague_AdminIdNull_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		mockLeague.setSeasonId(SEASON_ID);
		mockLeague.setAdminId(NULL_STR);
		leagueService.validateLeague(mockLeague);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.ADMIN_NOT_FOUND.toString());
	}
	
	@Test
	public void validateLeague_AdminIdEmpty_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		mockLeague.setSeasonId(SEASON_ID);
		mockLeague.setAdminId(EMPTY);
		leagueService.validateLeague(mockLeague);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.ADMIN_NOT_FOUND.toString());
	}
	@Test
	public void getLeagueByName_LeagueNameInvalid_LeagueNull() {
		when(leagueRepositoryMock.findByLeagueName(INVALID_LEAGUE_NAME)).thenReturn(null);
		final League league = leagueService.getLeagueByName(INVALID_LEAGUE_NAME);
		verify(leagueRepositoryMock).findByLeagueName(INVALID_LEAGUE_NAME);
		assertEquals(null, league);
	}
	
	@Test
	public void getLeagueByName_LeagueNameValid_League() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		when(leagueRepositoryMock.findByLeagueName(LEAGUE_NAME)).thenReturn(mockLeague);
		final League league = leagueService.getLeagueByName(LEAGUE_NAME);
		verify(leagueRepositoryMock).findByLeagueName(LEAGUE_NAME);
		assertNotNull(league);
		assertEquals(league.getLeagueName(), LEAGUE_NAME);
	}
	
	@Test
	public void createLeague_ValidLeague_Success() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		mockLeague.setSeasonId(SEASON_ID);
		mockLeague.setAdminId(ADMIN_ID);
		mockLeague.setId("ID_123");
		PlayerLeagueId leagueId = new PlayerLeagueId(mockLeague.getId(), mockLeague.getAdminId());
		PlayerLeague playerLeague = new PlayerLeague(leagueId);
		when(leagueRepositoryMock.findByLeagueName(LEAGUE_NAME)).thenReturn(null);
		when(leagueRepositoryMock.save(mockLeague)).thenReturn(mockLeague);
		when(playerLeagueRepositoryMock.save(playerLeague)).thenReturn(playerLeague);
		final League league = leagueService.createLeague(mockLeague);
		verify(leagueRepositoryMock).findByLeagueName(LEAGUE_NAME);
		verify(leagueRepositoryMock).save(mockLeague);
		//verify(playerLeagueRepositoryMock).save(playerLeague);
		assertNotNull(league);
	}
	
	@Test
	public void updateLeague_InvalidId_LeagueValidationException() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		mockLeague.setSeasonId(SEASON_ID);
		mockLeague.setAdminId(ADMIN_ID);
		mockLeague.setId(EMPTY);
		when(leagueRepositoryMock.findByLeagueName(LEAGUE_NAME)).thenReturn(null);
		when(leagueRepositoryMock.findOne(EMPTY)).thenReturn(null);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		leagueService.updateLeague(mockLeague);
		verify(leagueRepositoryMock).findByLeagueName(LEAGUE_NAME);
		verify(leagueRepositoryMock).findOne(EMPTY);
		}
	
	@Test
	public void updateLeague_ValidId_Success() {
		final League mockLeague = new League();
		mockLeague.setLeagueName(LEAGUE_NAME);
		mockLeague.setSeasonId(SEASON_ID);
		mockLeague.setAdminId(ADMIN_ID);
		mockLeague.setId("Test_123");
		when(leagueRepositoryMock.findByLeagueName(LEAGUE_NAME)).thenReturn(null);
		when(leagueRepositoryMock.findOne(mockLeague.getId())).thenReturn(mockLeague);
		when(leagueRepositoryMock.save(mockLeague)).thenReturn(mockLeague);
		final League league = leagueService.updateLeague(mockLeague);
		verify(leagueRepositoryMock).findByLeagueName(LEAGUE_NAME);
		verify(leagueRepositoryMock).findOne(mockLeague.getId());
		verify(leagueRepositoryMock).save(mockLeague);
		assertNotNull(league);
	}
	
	@Test
	public void getLeaguesForPlayer_PlayerIdNull_EmptyLeagueName() {
		when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(null)).thenReturn(null);
		final Set<LeagueName> leagueNameSet = leagueService.getLeaguesForPlayer(null);
		verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId(null);
		assertNotNull(leagueNameSet);
	}
	@Test
	
	public void getLeaguesForPlayer_PlayerIdEmpty_EmptyLeagueName() {
		when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(EMPTY)).thenReturn(null);
		final Set<LeagueName> leagueNameSet = leagueService.getLeaguesForPlayer(EMPTY);
		verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId(EMPTY);
		assertNotNull(leagueNameSet);
	}
	
	@Test
	public void getLeaguesForPlayer_InvalidPlayerId_EmptyLeagueName() {
		final String playerId = "Test";
		final List<String> mockLeagueIds = new ArrayList<>();
		when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(playerId)).thenReturn(mockLeagueIds);
		final Set<LeagueName> leagueNameSet = leagueService.getLeaguesForPlayer(playerId);
		verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId(playerId);
		assertNotNull(leagueNameSet);
	}
	
	@Test
	public void getLeaguesForPlayer_ValidPlayerIdAndLeagueNull_NullPointerException() {
		final String playerId = "123";
		final List<String> mockLeagueIds = new ArrayList<>();
		mockLeagueIds.add("League1");
		
		when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(playerId)).thenReturn(mockLeagueIds);
		when(leagueRepositoryMock.findAll(mockLeagueIds)).thenReturn(null);
		expectedException.expect(NullPointerException.class);
		leagueService.getLeaguesForPlayer(playerId);
		verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId(playerId);
		verify(leagueRepositoryMock).findAll(mockLeagueIds);
	}
	
	@Test
	public void getLeaguesForPlayer_ValidPlayerId_LeagueNames() {
		final String playerId = "123";
		final List<String> mockLeagueIds = new ArrayList<>();
		mockLeagueIds.add("League1");
		
		final List<League> leagueListMock = new ArrayList<>();
		final League leagueMock = new League();
		leagueMock.setLeagueName("Name1");
		leagueListMock.add(leagueMock);
		
		when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(playerId)).thenReturn(mockLeagueIds);
		when(leagueRepositoryMock.findAll(mockLeagueIds)).thenReturn(leagueListMock);
		final Set<LeagueName> leagueNames = leagueService.getLeaguesForPlayer(playerId);
		verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId(playerId);
		verify(leagueRepositoryMock).findAll(mockLeagueIds);
		assertNotNull(leagueNames);
		assertEquals("Name1", leagueNames.iterator().next().getLeagueName());
	}
	
	@Test
	public void getPlayersInLeague_LeagueIdNull_NullPointerException() {
		when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(null)).thenReturn(null);
		expectedException.expect(NullPointerException.class);
		leagueService.getPlayersInLeague(null);
		verify(playerLeagueRepositoryMock).findIdPlayerIdsByIdLeagueId(null);
	}
	
	@Test
	public void getPlayersInLeague_InvalidLeagueId_NullPointerException() {
		when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(null)).thenReturn(null);
		expectedException.expect(NullPointerException.class);
		leagueService.getPlayersInLeague(null);
		verify(playerLeagueRepositoryMock).findIdPlayerIdsByIdLeagueId(null);
	}
	
	@Test
	public void getPlayersInLeague_ValidLeagueId_ValidPlayers() {
		final String leagueId = "League1";
		final List<String> mockPlayersList = new ArrayList<>();
		mockPlayersList.add("player1");
		when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(leagueId)).thenReturn(mockPlayersList);
		final Set<String> playerSet = leagueService.getPlayersInLeague(leagueId);
		verify(playerLeagueRepositoryMock).findIdPlayerIdsByIdLeagueId(leagueId);
		assertNotNull(playerSet);
		assertEquals("player1", playerSet.iterator().next());
	}
	
	@Test
	public void joinLeague_PlayerLeagueNull_NullPointerException() {
		expectedException.expect(NullPointerException.class);
		leagueService.joinLeague(null);
	}
	
	@Test
	public void joinLeague_LeagueIdAndLeagueNameNull_LeagueValidationException() {
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		leagueService.joinLeague(playerLeagueMock);
	}
	
	@Test
	public void joinLeague_LeagueNameEmpty_LeagueValidationException() {
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		playerLeagueMock.setLeagueName(EMPTY);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		when(leagueRepositoryMock.findByLeagueName(EMPTY)).thenReturn(null);
		leagueService.joinLeague(playerLeagueMock);
		verify(leagueRepositoryMock).findByLeagueName(EMPTY);
	}
	@Test
	public void joinLeague_LeagueIdEmpty_LeagueValidationException() {
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		playerLeagueMock.setLeagueId(EMPTY);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		when(leagueRepositoryMock.findOne(playerLeagueMock.getLeagueId())).thenReturn(null);
		leagueService.joinLeague(playerLeagueMock);
		verify(leagueRepositoryMock).findOne(playerLeagueMock.getLeagueId());
	}
	
	
	
	@Test
	public void joinLeague_LeagueIdNotEmpty_LeagueValidationException() {
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		playerLeagueMock.setLeagueId("LeagueId1");
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		when(leagueRepositoryMock.findOne(playerLeagueMock.getLeagueId())).thenReturn(null);
		leagueService.joinLeague(playerLeagueMock);
		verify(leagueRepositoryMock).findOne(playerLeagueMock.getLeagueId());
	}
	
	@Test
	public void joinLeague_PasswordsDifferent_LeagueValidationException() {
		final String LEAGUE_ID = "Id_1";
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		playerLeagueMock.setLeagueName("Player1");
		playerLeagueMock.setPassword("Password2");
		
		final League leagueMock = new League();
		leagueMock.setId(LEAGUE_ID);
		leagueMock.setPassword("Password1");
		
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.INVALID_LEAGUE_PASSWORD.toString());
		when(leagueRepositoryMock.findByLeagueName(playerLeagueMock.getLeagueName())).thenReturn(leagueMock);
		when(leagueRepositoryMock.findOne(LEAGUE_ID)).thenReturn(leagueMock);
		leagueService.joinLeague(playerLeagueMock);
		verify(leagueRepositoryMock).findOne(playerLeagueMock.getLeagueId());
		verify(leagueRepositoryMock).findByLeagueName(playerLeagueMock.getLeagueName());
	}
	
	@Test
	public void joinLeague_LeagueIdNull_Success() {
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		playerLeagueMock.setLeagueId("Id_1");
		playerLeagueMock.setPlayerId("P_1");
		playerLeagueMock.setPassword("Password1");
		final League leagueMock = new League();
		when(leagueRepositoryMock.findOne(playerLeagueMock.getLeagueId())).thenReturn(leagueMock);
		when(playerLeagueRepositoryMock.save(playerLeagueMock)).thenReturn(playerLeagueMock);
		leagueService.joinLeague(playerLeagueMock);
		verify(leagueRepositoryMock).findOne(playerLeagueMock.getLeagueId());
	}
	
	@Test
	public void getLeagueById_LeagueIdNull_LeagueNull() {
		 final String leagueId = null;
		 when(leagueRepositoryMock.findOne(leagueId)).thenReturn(null);
		 final League league = leagueService.getLeagueById(null);
		 verify(leagueRepositoryMock).findOne(leagueId);
		 assertNull(league);
	}
	
	@Test
	public void getLeagueById_InvalidLeagueId_LeagueNull() {
		 final String leagueId = "11";
		 when(leagueRepositoryMock.findOne(leagueId)).thenReturn(null);
		 final League league = leagueService.getLeagueById(leagueId);
		 verify(leagueRepositoryMock).findOne(leagueId);
		 assertNull(league);
	}
	
	@Test
	public void getLeagueById_ValidLeagueId_League() {
		 final League leagueMock = new League();
		 leagueMock.setId("100");
		 final String leagueId = "League_1";
		 when(leagueRepositoryMock.findOne(leagueId)).thenReturn(leagueMock);
		 final League league = leagueService.getLeagueById(leagueId);
		 verify(leagueRepositoryMock).findOne(leagueId);
		 assertNotNull(league);
		 assertEquals("100", league.getId());
	}
	
	@Test
	public void getLeagueByName_LeagueNameNull_LeagueNull() {
		 when(leagueRepositoryMock.findByLeagueName(null)).thenReturn(null);
		 final League league = leagueService.getLeagueByName(null);
		 verify(leagueRepositoryMock).findByLeagueName(null);
		 assertNull(league);
	}
	
	@Test
	public void getLeagueByName_InvalidLeagueName_LeagueNull() {
		 final String leagueName = "11";
		 when(leagueRepositoryMock.findByLeagueName(leagueName)).thenReturn(null);
		 final League league = leagueService.getLeagueByName(leagueName);
		 verify(leagueRepositoryMock).findByLeagueName(leagueName);
		 assertNull(league);
	}
	
	@Test
	public void getLeagueByName_ValidLeagueName_League() {
		 final League leagueMock = new League();
		 leagueMock.setId("100");
		 final String leagueName = "League1";
		 when(leagueRepositoryMock.findByLeagueName(leagueName)).thenReturn(leagueMock);
		 final League league = leagueService.getLeagueByName(leagueName);
		 verify(leagueRepositoryMock).findByLeagueName(leagueName);
		 assertNotNull(league);
		 assertEquals("100", league.getId());
	}
	
	@Test
	public void removePlayerFromLeague_LeagueIdNull_LeagueValidationException() {
		final String leagueId = null;
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(null);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		leagueService.removePlayerFromLeague(null, null);
		verify(leagueRepositoryMock).findOne(leagueId);
	}
	
	@Test
	public void removePlayerFromLeague_InvalidLeagueId_LeagueValidationException() {
		final String leagueId = "Test";
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(null);
		expectedException.expect(LeagueValidationException.class);
		expectedException.expectMessage(LeagueExceptions.LEAGUE_NOT_FOUND.toString());
		leagueService.removePlayerFromLeague(leagueId, null);
		verify(leagueRepositoryMock).findOne(leagueId);
	}
	
	@Test
	public void removePlayerFromLeague_ValidLeagueIdAndPlayerIdNull_DeleteNotCalled() {
		final String leagueId = "Test";
		final League leagueMock = new League();
		leagueMock.setId("League_1");
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(leagueMock);
		leagueService.removePlayerFromLeague(leagueId, null);
		verify(leagueRepositoryMock).findOne(leagueId);
	}
	
	@Test
	public void removePlayerFromLeague_ValidLeagueIdAndPlayerId_Success() {
		final String leagueId = "League1";
		final String playerId = "player1";
		final League leagueMock = new League();
		leagueMock.setId("League_1");
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(leagueMock);
		when(playerLeagueRepositoryMock.findByIdLeagueIdAndIdPlayerId(leagueMock.getId(),playerId)).thenReturn(playerLeagueMock);
		leagueService.removePlayerFromLeague(leagueId, "Player1");
		verify(leagueRepositoryMock).findOne(leagueId);
	}
	
	
	@Test
	public void isLeagueValid_LeagueIdNull_False() {
		final String leagueId = null;
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(null);
		final boolean leagueFlag = leagueService.isLeagueValid(leagueId);
		verify(leagueRepositoryMock).findOne(leagueId);
		assertFalse(leagueFlag);
	}
	
	@Test
	public void isLeagueValid_LeagueId_True() {
		final String leagueId = "League1";
		final League leagueMock = new League();
		leagueMock.setId("100");
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(leagueMock);
		final boolean leagueFlag = leagueService.isLeagueValid(leagueId);
		verify(leagueRepositoryMock).findOne(leagueId);
		assertTrue(leagueFlag);
	}
	
	@Test
	public void getAllLeagues_Success() {
		final League leagueMock = new League();
		leagueMock.setId("100");
		final List<League> leagueList = new ArrayList<>();
		leagueList.add(leagueMock);
		when(leagueRepositoryMock.findAll()).thenReturn(leagueList);
		final Iterable<League> leagueListMock = leagueService.getAllLeagues();
		verify(leagueRepositoryMock).findAll();
		assertNotNull(leagueListMock);
		assertEquals("100", leagueListMock.iterator().next().getId());
	}
	
	@Test
	public void deleteLeague_LeagueIdNull_NullPointerException() {
		when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(null)).thenReturn(null);
		expectedException.expect(NullPointerException.class);
		leagueService.deleteLeague(null);
		verify(playerLeagueRepositoryMock).findIdPlayerIdsByIdLeagueId(null);
	}
	
	@Test
	public void deleteLeague_ValidLeagueId_Success() {
		final List<String> playerIdList = new ArrayList<>();
		playerIdList.add("1");
		playerIdList.add("2");
		final String leagueId = "L1";
		when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(leagueId)).thenReturn(playerIdList);
		final League leagueMock = new League();
		leagueMock.setId("100");
		when(leagueRepositoryMock.findOne(leagueId)).thenReturn(leagueMock);
		final PlayerLeague playerLeagueMock = new PlayerLeague(new PlayerLeagueId());
		when(playerLeagueRepositoryMock.findByIdLeagueIdAndIdPlayerId(leagueMock.getId(),"1")).thenReturn(playerLeagueMock);
		leagueService.deleteLeague(leagueId);
		verify(playerLeagueRepositoryMock).findByIdLeagueIdAndIdPlayerId(leagueMock.getId(),"1");
	}
	
	@Test
	public void getLeagueBySeasonId_SessionIdNull_LeagueNull() {
		when(leagueRepositoryMock.findLeagueBySeasonId(null)).thenReturn(null);
		List<League> leagueList = leagueService.getLeagueBySeasonId(null);
		verify(leagueRepositoryMock).findLeagueBySeasonId(null);
		assertEquals(leagueList, null);
	}
	
	@Test
	public void getLeagueBySeasonId_ValidSessionId_Success() {
		final String sessionId = "Id1";
		final League leagueMock = new League();
		leagueMock.setId("100");
		final List<League> leagueMockList = new ArrayList<>();
		leagueMockList.add(leagueMock);
		when(leagueRepositoryMock.findLeagueBySeasonId(sessionId)).thenReturn(leagueMockList);
		final List<League> leagueList = leagueService.getLeagueBySeasonId(sessionId);
		verify(leagueRepositoryMock).findLeagueBySeasonId(sessionId);
		assertNotNull(leagueList);
		assertEquals(leagueMockList, leagueList);
	}
	
}
