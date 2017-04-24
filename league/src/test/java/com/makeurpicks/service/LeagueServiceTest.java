package com.makeurpicks.service;

import com.makeurpicks.domain.*;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import com.makeurpicks.LeagueApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = LeagueApplication.class)
public class LeagueServiceTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private LeagueService leagueService;

    @Mock
    private LeagueRepository leagueRepositoryMock;

    @Mock
    private PlayerLeagueRepository playerLeagueRepositoryMock;


    @Test
    public void validateLeague_WhenLeagueIdNull_ThrowsLeagueValidationException() {

        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("LEAGUE_NAME_IS_NULL");

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.build();

        leagueService.validateLeague(league);
    }

    @Test
    public void validateLeague_WhenLeagueNameAlreadyExists_ThrowsLeagueValidationException() {

        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("LEAGUE_NAME_IN_USE");

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        leagueBuilder.withName("test name");

        final League league = leagueBuilder.build();

        when(leagueRepositoryMock.findByLeagueName(league.getLeagueName())).thenReturn(league);

        leagueService.validateLeague(league);

        verify(leagueRepositoryMock).findByLeagueName(league.getLeagueName());
    }

    @Test
    public void validateLeague_WhenSeasonIdNull_ThrowsLeagueValidationException() {

        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("SEASON_ID_IS_NULL");

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        leagueBuilder.withName("test name");

        final League league = leagueBuilder.build();

        when(leagueRepositoryMock.findByLeagueName(league.getLeagueName())).thenReturn(null);

        leagueService.validateLeague(league);

        verify(leagueRepositoryMock).findByLeagueName(league.getLeagueName());
    }

    @Test
    public void createLeague_ExpectsLeagueToBeCreated() {

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.withName("testname")
                .withAdminId("1")
                .withSeasonId("1")
                .withPassword("test")
                .build();

        final League cloneLeague = (League) SerializationUtils.clone(league);
        cloneLeague.setId("1");

        when(leagueRepositoryMock.save(league)).thenReturn(cloneLeague);

        final League savedLeague = leagueService.createLeague(league);

        Assert.assertNotNull(savedLeague.getId());

        verify(leagueRepositoryMock).save(league);
        verify(leagueRepositoryMock).findByLeagueName(league.getLeagueName());
    }

    @Test
    public void joinLeague_WhenLeagueIdNull_ThrowsLeagueValidationException() {

        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("LEAGUE_NOT_FOUND");

        final PlayerLeague playerLeague = createPlayerLeague();
        playerLeague.setLeagueId(null);

        leagueService.joinLeague(playerLeague);
    }



    @Test
    public void joinLeague_WhenLeagueNameNotFound_ThrowsLeagueValidationException() {

        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("LEAGUE_NOT_FOUND");

        when(leagueRepositoryMock.findByLeagueName("test name")).thenReturn(null);
        final PlayerLeague playerLeague = createPlayerLeague();

        leagueService.joinLeague(playerLeague);

        verify(leagueRepositoryMock).findByLeagueName("test name");
    }

    @Test
    public void joinLeague_ExpectsPlayerToBeAddedToLeague() {

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.withName("test name")
                .withAdminId("1")
                .withSeasonId("1")
                .withPassword("test")
                .build();

        when(leagueRepositoryMock.findByLeagueName("test name")).thenReturn(league);
        when(playerLeagueRepositoryMock.save(any(PlayerLeague.class))).thenReturn(createPlayerLeague());
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);

        final PlayerLeague playerLeague = createPlayerLeague();
        leagueService.joinLeague(playerLeague);

        verify(leagueRepositoryMock).findOne("1");
        verify(playerLeagueRepositoryMock).save(any(PlayerLeague.class));

    }

    @Test
    public void updateLeague_WhenLeagueIdNull_ThrowsLeagueValidationException() {

        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("LEAGUE_NOT_FOUND");

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.withName("testname")
                .withAdminId("1")
                .withSeasonId("1")
                .withPassword("test")
                .build();

        when(leagueRepositoryMock.findOne("1")).thenReturn(null);

        leagueService.updateLeague(league);

        verify(leagueRepositoryMock).findOne("1");
    }

    @Test
    public void updateLeague_ExpectsLeagueToBeUpdated() {
        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("LEAGUE_NOT_FOUND");

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.withName("testname")
                .withAdminId("1")
                .withSeasonId("1")
                .withPassword("test")
                .build();

        when(leagueRepositoryMock.findOne(league.getId())).thenReturn(null);

        League updatedLeague = leagueService.updateLeague(league);

        Assert.assertNotNull(updatedLeague);

        verify(leagueRepositoryMock).findOne(league.getId());
    }


    @Test
    public void getLeaguesForPlayer_ExpectsLeaguesToBeReturned() {

        final List<String> leagueIds = new ArrayList<>();
        leagueIds.add("1");

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.withName("testname")
                .withAdminId("1")
                .withSeasonId("1")
                .withPassword("test")
                .build();

        final List<League> leagues = new ArrayList<>();

        leagues.add(league);

        when(playerLeagueRepositoryMock.findIdLeagueIdsByIdPlayerId("1")).thenReturn(leagueIds);
        when(leagueRepositoryMock.findAll(leagueIds)).thenReturn(leagues);

        Set<LeagueName> leagueNames = leagueService.getLeaguesForPlayer("1");
        assertEquals(1, leagueNames.size());

        verify(playerLeagueRepositoryMock).findIdLeagueIdsByIdPlayerId("1");
        verify(leagueRepositoryMock).findAll(leagueIds);
    }

    @Test
    public void addPlayerToLeague_ExpectsPlayerToBeAdded() {

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.withName("testname")
                .withAdminId("1")
                .withSeasonId("1")
                .withPassword("test")
                .build();

        when(playerLeagueRepositoryMock.save(any(PlayerLeague.class))).thenReturn(createPlayerLeague());
        PlayerLeague playerLeague = leagueService.addPlayerToLeague(league, "1");

        assertNotNull(playerLeague);

        verify(playerLeagueRepositoryMock).save(any(PlayerLeague.class));
    }

    @Test
    public void removePlayerFromLeague_WhenLeagueIdNotExist_ThroewsLeagueValidationException() {

        expectedException.expect(LeagueValidationException.class);
        expectedException.expectMessage("LEAGUE_NOT_FOUND");
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(null);
        leagueService.removePlayerFromLeague("1", "1");
    }


    @Test
    public void removePlayerFromLeague_WhenLeagueIdExists_RemovesPlayer() {

        final LeagueBuilder leagueBuilder = new LeagueBuilder();
        final League league = leagueBuilder.withName("testname")
                .withAdminId("1")
                .withSeasonId("1")
                .withPassword("test")
                .build();

        PlayerLeague playerLeague = createPlayerLeague();
        when(leagueRepositoryMock.findOne(anyString())).thenReturn(league);
        when(playerLeagueRepositoryMock.findByIdLeagueIdAndIdPlayerId(anyString(), anyString())).thenReturn(playerLeague);

        leagueService.removePlayerFromLeague("1", "1");

        verify(playerLeagueRepositoryMock).delete(playerLeague);
    }

    /**
     * Builds and returns dummy {@code PlayerLeague}
     *
     * @return
     */
    private PlayerLeague createPlayerLeague() {

        // TODO - No Builder for this
        final PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId("1", "1"));
        playerLeague.setLeagueId("1");
        playerLeague.setLeagueName("test name");
        playerLeague.setPassword("test");
        playerLeague.setPlayerId("1");

        return playerLeague;
    }

}
