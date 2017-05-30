/**
 * 
 */
package com.makeurpicks.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.domain.Season;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import com.makeurpicks.repository.SeasonRepository;

/**
 * @author tarun
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LeagueApplicationTest {

	@Autowired
	WebApplicationContext context;

	private MockMvc mockMvc;

	@Autowired
	private PlayerLeagueRepository playerLeagueRepository;

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private SeasonRepository seasonRepository;

	private League league1,league2,league3;
	private PlayerLeague playerLeague1,playerLeague2,playerLeague3;
	private List<League> allLeagues = new ArrayList<League>();
	private List<PlayerLeague> allPlayerLeagues = new ArrayList<PlayerLeague>();
	private Season season;
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		season = new Season();
		
		season.setEndYear(2017);
		season.setId("1");
		season.setLeagueType("league");
		season.setStartYear(2016);
		
		league1 = new LeagueBuilder()
				.withAdminId("1")
				.withName("pickem")
				.withPassword("football")
				.withSeasonId("1")
				.build();

		league2 = new LeagueBuilder()
				.withAdminId("1")
				.withName("suicide")
				.withPassword("football")
				.withSeasonId("1")
				.build();

		league3 = new LeagueBuilder()
				.withAdminId("1")
				.withName("superbowl")
				.withPassword("football")
				.withSeasonId("1")
				.build();
		
		playerLeague1 = new PlayerLeague(new PlayerLeagueId("1", "1"));
		playerLeague2 = new PlayerLeague(new PlayerLeagueId("2", "2"));
		playerLeague3 = new PlayerLeague(new PlayerLeagueId("3", "3"));
		playerLeague1.setPassword(league1.getPassword());
		playerLeague2.setPassword(league2.getPassword());
		playerLeague3.setPassword(league3.getPassword());

		allLeagues.add(league1);
		allLeagues.add(league2);
		allLeagues.add(league3);
		
		allPlayerLeagues.add(playerLeague1);
		allPlayerLeagues.add(playerLeague2);
		allPlayerLeagues.add(playerLeague3);
		insertData();
	}

	private void insertData(){
		leagueRepository.save(allLeagues);
		playerLeagueRepository.save(allPlayerLeagues);
		seasonRepository.save(season);
	}
	
	@Test
	public void testGetAllLeagues() throws Exception{
		mockMvc.perform(get("/leagues/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].leagueName", is(league1.getLeagueName())))
				.andDo(print());
	}
	
	@Test
	public void testGetLeagueById() throws Exception{
		mockMvc.perform(get("/leagues/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is("1")))
		.andExpect(jsonPath("$.password", is(league1.getPassword())))
		.andDo(print());
	}
	
	@Test
	public void testGetLeagueTypes() throws Exception{
		mockMvc.perform(get("/leagues/types"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0]", is("pickem")))
		.andExpect(jsonPath("$[1]", is("suicide")))
		.andDo(print());
	}
	
	@Test
	public void testGetLeagueBySeasonId() throws Exception {
		mockMvc.perform(get("/leagues/seasons/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id", is("1")))
		.andExpect(jsonPath("$[1].id", is("2")))
		.andDo(print()).andReturn();
	}
	
	@Test
	public void testCreateLeague() throws Exception{
		String json=	"{\"id\":\"4c1326d5-9ffb-49e8-8b84-a86a98e15275\",\"leagueName\":\"newLeague\",\"paidFor\":0,\"money\":false,\"free\":false,\"active\":false,\"password\":\"football\",\"spreads\":false,\"doubleEnabled\":true,\"entryFee\":0.0,\"weeklyFee\":0.0,\"firstPlacePercent\":0,\"secondPlacePercent\":0,\"thirdPlacePercent\":0,\"fourthPlacePercent\":0,\"fifthPlacePercent\":0,\"doubleType\":0,\"banker\":false,\"seasonId\":\"1\",\"adminId\":\"1\"}";
		mockMvc.perform(post("/leagues/").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(()->"admin"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is("4c1326d5-9ffb-49e8-8b84-a86a98e15275")))
		.andExpect(jsonPath("$.leagueName", is("newLeague")))
		.andDo(print());
	} 
	
	@Test
	public void testCreateLeagueForBadRequest() throws Exception{
		String json=	"{\"id\":\"4c1326d5-9ffb-49e8-8b84-a86a98e15275\",\"leagueName\":\"pickem\",\"paidFor\":0,\"money\":false,\"free\":false,\"active\":false,\"password\":\"football\",\"spreads\":false,\"doubleEnabled\":true,\"entryFee\":0.0,\"weeklyFee\":0.0,\"firstPlacePercent\":0,\"secondPlacePercent\":0,\"thirdPlacePercent\":0,\"fourthPlacePercent\":0,\"fifthPlacePercent\":0,\"doubleType\":0,\"banker\":false,\"seasonId\":\"1\",\"adminId\":\"1\"}";
		mockMvc.perform(post("/leagues/").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(()->"admin"))
		.andExpect(status().isBadRequest())
		.andDo(print());
	} 
	
	@Test
	public void testUpdateLeague() throws Exception{
		String json ="{\"id\":\"1\",\"leagueName\":\"pickemUpdate\",\"paidFor\":0,\"money\":false,\"free\":false,\"active\":false,\"password\":\"football\",\"spreads\":false,\"doubleEnabled\":true,\"entryFee\":0.0,\"weeklyFee\":0.0,\"firstPlacePercent\":0,\"secondPlacePercent\":0,\"thirdPlacePercent\":0,\"fourthPlacePercent\":0,\"fifthPlacePercent\":0,\"doubleType\":0,\"banker\":false,\"seasonId\":\"1\",\"adminId\":\"1\"}";
		mockMvc.perform(put("/leagues/").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is("1")))
		.andExpect(jsonPath("$.leagueName", is("pickemUpdate")))
		.andDo(print());
	}
	
	@Test
	public void testGetLeaguesForPlayer() throws Exception {
		String result = mockMvc.perform(get("/leagues/player/1"))
				.andExpect(status().isOk())
				.andDo(print()).andReturn().getResponse().getContentAsString();
		assertNotNull(result);
		assertThat(result.contains("\"leagueId\":\"1\",\"seasonId\":\"1\""));
		
	}
	
	@Test
	public void testAddPlayerToLeague() throws Exception {
		PlayerLeague newPlayerLeague = new PlayerLeague(new PlayerLeagueId("1", "1000"));
		newPlayerLeague.setPassword(league1.getPassword());
		String json = mapper.writeValueAsString(newPlayerLeague);
		mockMvc.perform(post("/leagues/player").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(()->"1000"))
		.andExpect(status().isOk())
		.andDo(print());
	}
	
	@Test
	public void testGetLeagueByName() throws Exception{
		mockMvc.perform(get("/leagues/name/"+league2.getLeagueName()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is("2")))
		.andExpect(jsonPath("$.leagueName", is(league2.getLeagueName())))
		.andDo(print());
	}
	
	@Test
	public void testRemovePlayerFromLeague() throws Exception {
		PlayerLeague newPlayerLeague = new PlayerLeague(new PlayerLeagueId("3", "3"));
		newPlayerLeague.setPassword(league1.getPassword());
		String json = mapper.writeValueAsString(newPlayerLeague);
		mockMvc.perform(delete("/leagues/player").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(()->"admin"))
		.andExpect(status().isOk())
		.andDo(print());
	}
	
	@Test
	public void testGetPlayersInLeague() throws Exception {
		String result = mockMvc.perform(get("/leagues/player/2/"))
		.andExpect(status().isOk())
		.andDo(print()).andReturn().getResponse().getContentAsString();
		assertThat(result.contains("\"leagueName\":\"suicide\",\"leagueId\":\"2\",\"seasonId\":\"1\""));
	}
}
