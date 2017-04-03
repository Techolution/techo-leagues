package com.makeurpicks.service;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.method.P;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(MockitoJUnitRunner.class)
//@SpringApplicationConfiguration(classes = LeagueApplication.class)
@SpringBootTest
public class LeagueServiceTest {
    private static final String SEASON_ID = "s1";
    private League league;
    private PlayerLeague playerLeague;
    private final static String LEAGUE_ID = "league1";
    private final static String LEAGUE_NAME = "Techoleague";
    private final static String PASSWORD = "abc#123";
    private final static String PLAYER_ID = "P0001";
    private final static String ADMIN_ID = "admin1";
    @Mock
    private LeagueRepository leagueRepositoryMock;

    @Mock
    private PlayerLeagueRepository playerLeagueRepositoryMock;

    @Autowired
    @InjectMocks
    private LeagueService leagueService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void validateLeague_whenLeagueNameIsNull_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NAME_IS_NULL.name());
        league = getValidLeague();
        league.setLeagueName(null);
        leagueService.validateLeague(league);
    }

    @Test
    public void validateLeague_whenLeagueNameIsEmpty_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NAME_IS_NULL.name());
        league = getValidLeague();
        league.setLeagueName("");
        leagueService.validateLeague(league);
    }

    @Test
    public void validateLeague_whenLeagueNameInUse_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NAME_IN_USE.name());
        league = getValidLeague();
        when(leagueRepositoryMock.findByLeagueName(Mockito.anyString())).thenReturn(league);
        leagueService.validateLeague(league);
    }

    @Test
    public void validateLeague_whenSeasonIdIsNull_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.SEASON_ID_IS_NULL.name());
        league = getValidLeague();
        league.setSeasonId(null);
        leagueService.validateLeague(league);
    }

    @Test
    public void validateLeague_whenSeasonIdIsEmpty_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.SEASON_ID_IS_NULL.name());
        league = getValidLeague();
        league.setSeasonId("");
        leagueService.validateLeague(league);
    }

    @Test
    public void validateLeague_whenAdminIdIsNull_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.ADMIN_NOT_FOUND.name());
        league = getValidLeague();
        league.setAdminId(null);
        leagueService.validateLeague(league);
    }

    @Test
    public void validateLeague_whenAdminIdIsEmpty_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.ADMIN_NOT_FOUND.name());
        league = getValidLeague();
        league.setAdminId("");
        leagueService.validateLeague(league);
    }

    @Test
    public void addPlayerToLeague_withValidPlayerLeague_success() {
        league = getValidLeague();
        playerLeague = getValidPlayerLeague(league);
        when(playerLeagueRepositoryMock.save((PlayerLeague) anyObject())).thenReturn(playerLeague);
        playerLeague = leagueService.addPlayerToLeague(league, PLAYER_ID);
        assertEquals(playerLeague.getLeagueId(), LEAGUE_ID);
        assertEquals(playerLeague.getLeagueName(), LEAGUE_NAME);
        assertEquals(playerLeague.getPassword(), PASSWORD);
        assertEquals(playerLeague.getPlayerId(), PLAYER_ID);
    }

    @Test
    public void createLeague_withValidInput_success() {
        league = getValidLeague();
        playerLeague = getValidPlayerLeague(league);
        when(playerLeagueRepositoryMock.save((PlayerLeague) anyObject())).thenReturn(playerLeague);
        when(leagueRepositoryMock.save((League) anyObject())).thenReturn(league);
        league = leagueService.createLeague(league);
        assertEquals(league.getId(), LEAGUE_ID);
        assertEquals(league.getLeagueName(), LEAGUE_NAME);
        assertEquals(league.getPassword(), PASSWORD);
        verify(leagueRepositoryMock).save(league);
    }

    @Test
    public void updateLeague_whenLeagueNotFound_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NOT_FOUND.name());
        league = getValidLeague();
        when(leagueRepositoryMock.getOne(anyString())).thenReturn(null);
        leagueService.updateLeague(league);
        verify(leagueRepositoryMock).save(league);
    }

    @Test
    public void updateLeague_whenValidInput_success() {
        league = getValidLeague();
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        when(leagueRepositoryMock.save((League) anyObject())).thenReturn(league);
        league = leagueService.updateLeague(league);
        assertEquals(league.getId(), LEAGUE_ID);
        assertEquals(league.getLeagueName(), LEAGUE_NAME);
        assertEquals(league.getPassword(), PASSWORD);
        verify(leagueRepositoryMock).save(league);
    }

    @Test
    public void getLeaguesForPlayer_whenNoLeaguesAvailable_returnsEmptyList() {
        when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(anyString())).thenReturn(Arrays.asList());
        assertEquals(leagueService.getLeaguesForPlayer(PLAYER_ID).size(), 0);
        verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId(PLAYER_ID);
    }

    @Test
    public void getLeaguesForPlayer_whenLeaguesAvailable_returnsList() {
        when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId(anyString())).thenReturn(Arrays.asList(LEAGUE_ID));
        league = getValidLeague();
        when(leagueRepositoryMock.findAll((Iterable<String>) anyObject())).thenReturn(Arrays.asList(league));
        Set<LeagueName> leagueNames = leagueService.getLeaguesForPlayer(PLAYER_ID);
        assertEquals(leagueNames.size(), 1);
        assertTrue(leagueNames.contains(new LeagueName(league)));
        verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId(PLAYER_ID);
    }

    @Test
    public void getPlayersInLeague_whenNoPlayerExists_returnsEmptyList() {
        when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(anyString())).thenReturn(Arrays.asList());
        assertEquals(leagueService.getPlayersInLeague(LEAGUE_ID).size(), 0);
        verify(playerLeagueRepositoryMock).findIdPlayerIdsByIdLeagueId(LEAGUE_ID);
    }

    @Test
    public void getPlayersInLeague_whenPlayerExists_returnsList() {
        when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(anyString())).thenReturn(Arrays.asList(PLAYER_ID));
        Set<String> playerIds = leagueService.getPlayersInLeague(LEAGUE_ID);
        assertEquals(playerIds.size(), 1);
        assertTrue(playerIds.contains(PLAYER_ID));
        verify(playerLeagueRepositoryMock).findIdPlayerIdsByIdLeagueId(LEAGUE_ID);
    }

    @Test
    public void joinLeague_whenLeagueIdNull_success() {
        league = getValidLeague();
        when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(league);
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        playerLeague = getValidPlayerLeague(league);
        playerLeague.setLeagueId(null);
        leagueService.joinLeague(playerLeague);
        verify(leagueRepositoryMock).findOne(league.getId());
        verify(leagueRepositoryMock).findByLeagueName(league.getLeagueName());
    }


    @Test
    public void joinLeague_whenLeagueIdNameNull_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NOT_FOUND.name());
        playerLeague = getValidPlayerLeague(getValidLeague());
        playerLeague.setLeagueName(null);
        playerLeague.setLeagueId(null);
        leagueService.joinLeague(playerLeague);
    }

    @Test
    public void joinLeague_whenLeagueIdNullNameNotNull_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NOT_FOUND.name());
        when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(null);
        playerLeague = getValidPlayerLeague(getValidLeague());
        playerLeague.setLeagueId(null);
        leagueService.joinLeague(playerLeague);
    }

    @Test
    public void joinLeague_whenLeagueIdNullNameNotNullLeagueByLeagueNameNotNull_throwsLeagueValidationException() {
        league = getValidLeague();
        when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(league);
        playerLeague = getValidPlayerLeague(getValidLeague());
        playerLeague.setLeagueId(null);
        when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(league);
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        leagueService.joinLeague(playerLeague);
    }

    @Test
    public void joinLeague_whenLeagueNotFound_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NOT_FOUND.name());
        leagueService.joinLeague(LEAGUE_ID, PLAYER_ID, PASSWORD);
    }

    @Test
    public void joinLeague_whenLeaguePasswordIsInvalid_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.INVALID_LEAGUE_PASSWORD.name());
        league = getValidLeague();
        league.setPassword("123");
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        leagueService.joinLeague(LEAGUE_ID, PLAYER_ID, PASSWORD);
    }

    @Test
    public void innerJoinLeague_whenValidInput_success() {
        league = getValidLeague();
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        leagueService.joinLeague(LEAGUE_ID, PLAYER_ID, PASSWORD);
        verify(leagueRepositoryMock).findOne(league.getId());
    }

    @Test
    public void joinLeague_whenValidInput_success() {
        league = getValidLeague();
        when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(league);
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        playerLeague = getValidPlayerLeague(league);
        leagueService.joinLeague(playerLeague);
        verify(leagueRepositoryMock).findOne(league.getId());
    }

    @Test
    public void getLeagueById_whenValidLeagueId_success() {
        league = getValidLeague();
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        final League result = leagueService.getLeagueById(LEAGUE_ID);
        assertEquals(result.getId(), LEAGUE_ID);
        assertEquals(result.getLeagueName(), LEAGUE_NAME);
        assertEquals(result.getPassword(), PASSWORD);
        verify(leagueRepositoryMock).findOne(LEAGUE_ID);
    }

    @Test
    public void getLeagueByName_whenValidLeagueName_success() {
        league = getValidLeague();
        when(leagueRepositoryMock.findByLeagueName(anyString())).thenReturn(league);
        final League result = leagueService.getLeagueByName(LEAGUE_NAME);
        assertEquals(result.getId(), LEAGUE_ID);
        assertEquals(result.getLeagueName(), LEAGUE_NAME);
        assertEquals(result.getPassword(), PASSWORD);
        verify(leagueRepositoryMock).findByLeagueName(LEAGUE_NAME);
    }

    @Test
    public void removePlayerFromLeague_whenLeagueNotFound_throwsLeagueValidationException() {
        expectedEx.expect(LeagueValidationException.class);
        expectedEx.expectMessage(LeagueValidationException.LeagueExceptions.LEAGUE_NOT_FOUND.name());
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(null);
        leagueService.removePlayerFromLeague(LEAGUE_ID, PLAYER_ID);
        verify(leagueRepositoryMock).findOne(LEAGUE_ID);
    }

    @Test
    public void removePlayerFromLeague_whenValidInput_success() {
        league = getValidLeague();
        playerLeague = getValidPlayerLeague(league);
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        when(playerLeagueRepositoryMock.findByIdLeagueIdAndIdPlayerId(anyString(), anyString())).thenReturn(playerLeague);
        leagueService.removePlayerFromLeague(LEAGUE_ID, PLAYER_ID);
        verify(leagueRepositoryMock).findOne(LEAGUE_ID);
        verify(playerLeagueRepositoryMock).delete(playerLeague);
    }

    @Test
    public void getAllLeagues_whenNoLeaguesPresent_returnsEmptyList() {
        when(leagueRepositoryMock.findAll()).thenReturn(Arrays.asList());
        Iterable<League> allLeagues = leagueService.getAllLeagues();
        int count = 0;
        for (League l : allLeagues) {
            count++;
        }
        assertEquals(count, 0);
    }

    @Test
    public void getAllLeagues_whenLeaguesPresent_returnsList() {
        league = getValidLeague();
        when(leagueRepositoryMock.findAll()).thenReturn(Arrays.asList(league));
        Iterable<League> allLeagues = leagueService.getAllLeagues();
        int count = 0;
        League leageInList = null;
        for (League l : allLeagues) {
            count++;
            leageInList = l;
        }
        assertEquals(count, 1);
        assertTrue(league.getId().equals(leageInList.getId()));
    }

    @Test
    public void deleteLeague_whenValidLeagueId_success() {
        when(playerLeagueRepositoryMock.findIdPlayerIdsByIdLeagueId(anyString())).thenReturn(Arrays.asList(PLAYER_ID));
        Mockito.doNothing().when(leagueRepositoryMock).delete(anyString());
        leagueService.deleteLeague(LEAGUE_ID);
        verify(leagueRepositoryMock).delete(LEAGUE_ID);
    }

    @Test
    public void getLeagueBySeasonId_whenNoLeaguesPresent_returnsEmptyList() {
        when(leagueRepositoryMock.findLeagueBySeasonId(anyString())).thenReturn(Arrays.asList());
        assertEquals(leagueService.getLeagueBySeasonId(SEASON_ID).size(), 0);
        verify(leagueRepositoryMock).findLeagueBySeasonId(SEASON_ID);
    }

    @Test
    public void getLeagueBySeasonId_whenLeaguesPresent_returnsList() {
        league = getValidLeague();
        when(leagueRepositoryMock.findLeagueBySeasonId(anyString())).thenReturn(Arrays.asList(league));
        List<League> leagues = leagueService.getLeagueBySeasonId(SEASON_ID);
        assertEquals(leagues.size(), 1);
        assertTrue(leagues.contains(league));
        verify(leagueRepositoryMock).findLeagueBySeasonId(SEASON_ID);
    }

    private League getValidLeague() {
        League league = new League();
        league.setId(LEAGUE_ID);
        league.setLeagueName(LEAGUE_NAME);
        league.setPassword(PASSWORD);
        league.setSeasonId(SEASON_ID);
        league.setAdminId(ADMIN_ID);
        return league;
    }

    private PlayerLeague getValidPlayerLeague(League league) {
        PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId(), PLAYER_ID));
        playerLeague.setLeagueId(league.getId());
        playerLeague.setLeagueName(league.getLeagueName());
        playerLeague.setPassword(league.getPassword());
        playerLeague.setPlayerId(PLAYER_ID);
        return playerLeague;
    }
}
