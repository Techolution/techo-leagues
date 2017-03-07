package com.makeurpicks.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

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

	@Autowired
	@InjectMocks
	private LeagueService leagueService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private LeagueRepository leagueRepository;

	@Mock
	private PlayerLeagueRepository playerLeagueRepository;

	@Test(expected = NullPointerException.class) 
	public void testCreateLeague_expect_nullPointerException_whenPassNull() {
		leagueService.createLeague(null);

		/*
		 * Note: 
		 * 
		 * In real null check should happen. So it should not throw
		 * NullPointerException when the argument is passed as null. This should
		 * have been checked in the validation with proper messages. Hence the
		 * current test case is not a real-time scenario.
		 */
	}


	@Test
	public void CreateLeague_noLeagueName_throwsLeagueException(){
		League league = new League();
		league.setId(null);
		thrown.expect(LeagueValidationException.class);
		thrown.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_NULL.toString());
		league = leagueService.createLeague(league);
	}

	@Test
	public void CreateLeague_noSeasionId_throwsLeagueException(){
		League league = new League();
		league.setId(null);
		league.setLeagueName("League-one");
		thrown.expect(LeagueValidationException.class);
		thrown.expectMessage(LeagueExceptions.SEASON_ID_IS_NULL.toString());
		league = leagueService.createLeague(league);
	}

	@Test
	public void CreateLeague_noAdminId_throwsLeagueException(){
		League league = new League();
		league.setId(null);
		league.setLeagueName("League-one");
		league.setSeasonId(UUID.randomUUID().toString());
		thrown.expect(LeagueValidationException.class);
		thrown.expectMessage(LeagueExceptions.ADMIN_NOT_FOUND.toString());
		league = leagueService.createLeague(league);
	}


	@Test
	public void testGetLeaguesForPlayer_verifyLeagueNames_correctlyRetriving(){

		List<String> leagueIds = new ArrayList<>();
		leagueIds.add("1");
		leagueIds.add("2");

		League l1 = new League();
		l1.setLeagueName("L1");
		l1.setId("1");
		l1.setSeasonId("S1");

		League l2 = new League();
		l2.setLeagueName("L2");
		l2.setId("2");
		l2.setSeasonId("S2");

		League l3 = new League();
		l3.setLeagueName("L3");
		l3.setId("3");
		l3.setSeasonId("S3");

		List<League> leagues = new ArrayList<>();
		leagues.add(l1);
		leagues.add(l2);
		//leagues.add(l3);

		when(playerLeagueRepository.findIdLeagueIdsByIdPlayerId("1")).thenReturn(leagueIds);
		when(leagueRepository.findAll(leagueIds)).thenReturn(leagues);


		Set<LeagueName> lNames = leagueService.getLeaguesForPlayer("1");

		Assert.assertSame(2, lNames.size());
		Assert.assertTrue(lNames.contains(new LeagueName(l1)));
		Assert.assertTrue(lNames.contains(new LeagueName(l2)));
		Assert.assertFalse(lNames.contains(new LeagueName(l3)));

		verify(playerLeagueRepository).findIdLeagueIdsByIdPlayerId("1");

		verify(leagueRepository).findAll(leagueIds);
	}


	@Test
	public void testCreateLeague(){

		League l1 = new League();
		l1.setLeagueName("L1");
		l1.setId("1");
		l1.setSeasonId("S1");
		l1.setAdminId("ADM1");

		League returnLeague = leagueService.createLeague(l1);

		verify(leagueRepository).save(l1);

		Assert.assertSame(l1, returnLeague);
	}

	@Test
	public void testUpdateLeague() throws LeagueValidationException {

		League leagueBefore = new League();
		leagueBefore.setLeagueName("L1");
		leagueBefore.setId("1");
		leagueBefore.setSeasonId("S1");
		leagueBefore.setAdminId("ADM1");

		League leagueAfter= new League();
		leagueAfter.setLeagueName("L1");
		leagueAfter.setId("1");
		leagueAfter.setSeasonId("S2");
		leagueAfter.setAdminId("ADM2");

		when(leagueRepository.findOne(leagueBefore.getId())).thenReturn(leagueAfter);
		League leagueDs = leagueService.updateLeague(leagueBefore);

		verify(leagueRepository).findOne(leagueBefore.getId());
		verify(leagueRepository).save(leagueAfter);

		Assert.assertSame(leagueDs, leagueBefore);

	}

	@Test
	public void testGetPlayersInLeague() throws LeagueValidationException {

		String leagueId = "L1";

		leagueService.getPlayersInLeague(leagueId);
		verify(playerLeagueRepository).findIdPlayerIdsByIdLeagueId(leagueId);
	}

	
	/*@Test
	public void testJoinLeague_wherePlayerLeagueIsNull_throwLeagueValidationException_LeagueNotFound(){
		
		PlayerLeague playerLeague = new PlayerLeague(null);
		thrown.expect(LeagueValidationException.class);
		thrown.expectMessage(LeagueExceptions.LEAGUE_NAME_IS_NULL.toString());
		
		leagueService.joinLeague(playerLeague);
		
		verify(leagueRepository).findOne(null);
	}*/
	
	@Test
	public void testJoinLeague(){
		
		PlayerLeagueId id = new PlayerLeagueId("L1", "P1");
		PlayerLeague playerLeague = new PlayerLeague(id);
		playerLeague.setLeagueName("LeagueName");
		
		League league = new League();
		
		
		when(leagueRepository.findOne(playerLeague.getLeagueId())).thenReturn(league);
		
		leagueService.joinLeague(playerLeague);
		
		verify(leagueRepository).findOne(playerLeague.getLeagueId());
	}
	
}
