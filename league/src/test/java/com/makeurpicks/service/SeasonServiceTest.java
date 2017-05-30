package com.makeurpicks.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.SeasonBuilder;
import com.makeurpicks.repository.SeasonRepository;

/**
 * @author tarun
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SeasonServiceTest {

	@Mock
	private SeasonRepository seasonRepositoryMock;

	@InjectMocks
	private SeasonService seasonService = new SeasonService();

	private Season season;

	@Before
	public void setup()	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int currentYear = calendar.get(Calendar.YEAR);
		season =  new SeasonBuilder(UUID.randomUUID().toString())
				.withStartYear(currentYear)
				.withEndYear(currentYear+1)
				.withLeagueType(LeagueType.pickem)
				.build();
	}

	@Test
	public void testGetCurrentSeasons() {
		when(seasonRepositoryMock.save(season)).thenReturn(season);
		when(seasonRepositoryMock.getSeasonsByLeagueType(LeagueType.pickem.toString())).thenReturn(Arrays.asList(season));

		season = seasonService.createSeason(season);
		Assert.assertTrue(seasonService.getCurrentSeasons().contains(season));
	}

	@Test
	public void testUpdateSeason() {
		when(seasonRepositoryMock.save(season)).thenReturn(season);
		assertNotNull(seasonService.updateSeason(season));
	}


}
