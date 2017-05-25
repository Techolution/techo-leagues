package com.makeurpicks.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.service.LeagueService;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class LeagueControllerTest {

	@InjectMocks
	private LeagueController leagueController;

	@Mock
	private LeagueService leagueService;

	private MockMvc mockMvc;

	private League league1;
	private League league2;
	private League league3;
	private Principal principal;

	private ObjectMapper mapper = new ObjectMapper();
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(leagueController).build();
		stubData();
	}

	private void stubData()
	{
		principal = new Principal() {
			@Override
			public String getName() {
				return "ADMIN";
			}
		};

		String player1Id = "1";

		league1 = new LeagueBuilder()
				.withAdminId(player1Id)
				.withName("pickem")
				.withPassword("football")
				.withSeasonId("1")
				.build();

		league2 = new LeagueBuilder()
				.withAdminId(player1Id)
				.withName("suicide")
				.withPassword("football")
				.withSeasonId("1")
				.build();

		league3 = new LeagueBuilder()
				.withAdminId(player1Id)
				.withName("superbowl")
				.withPassword("football")
				.withSeasonId("1")
				.build();

		List<League> allLeagues = new ArrayList<>();
		allLeagues.add(league1);
		allLeagues.add(league2);
		allLeagues.add(league3);

		when(leagueService.getAllLeagues()).thenReturn(allLeagues);
		when(leagueService.getLeagueById(league1.getId())).thenReturn(league1);
		when(leagueService.getLeagueById(league2.getId())).thenReturn(league2);
		when(leagueService.getLeagueById(league3.getId())).thenReturn(league3);
		when(leagueService.getLeagueBySeasonId(anyString())).thenReturn(allLeagues);
	}


	@Test
	public void testGetAllLeague() throws Exception {
		mockMvc.perform(get("/leagues/"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id", is(league1.getId())))
		.andExpect(jsonPath("$", hasSize(3)))
		.andDo(print()).andReturn();
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
	public void testGetLeagueById() throws Exception{
		mockMvc.perform(get("/leagues/"+league1.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(league1.getId())))
		.andExpect(jsonPath("$.leagueName", is(league1.getLeagueName())))
		.andDo(print());
	}

	@Test
	public void testGetLeagueBySeasonId() throws Exception {
		mockMvc.perform(get("/leagues/seasons/seasonId"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id", is(league1.getId())))
		.andExpect(jsonPath("$[1].id", is(league2.getId())))
		.andExpect(jsonPath("$", hasSize(3)))
		.andDo(print()).andReturn();
	}

	@Test
	public void testCreateLeague() throws Exception{
		String json=	"{\"id\":\"4c1326d5-9ffb-49e8-8b84-a86a98e15275\",\"leagueName\":\"pickem\",\"paidFor\":0,\"money\":false,\"free\":false,\"active\":false,\"password\":\"football\",\"spreads\":false,\"doubleEnabled\":true,\"entryFee\":0.0,\"weeklyFee\":0.0,\"firstPlacePercent\":0,\"secondPlacePercent\":0,\"thirdPlacePercent\":0,\"fourthPlacePercent\":0,\"fifthPlacePercent\":0,\"doubleType\":0,\"banker\":false,\"seasonId\":\"1\",\"adminId\":\"1\"}";
		when(leagueService.createLeague((League)anyObject())).thenReturn(league1);
		mockMvc.perform(post("/leagues/").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(principal))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(league1.getId())))
		.andExpect(jsonPath("$.leagueName", is(league1.getLeagueName())))
		.andDo(print());
	} 

	@Test
	public void testUpdateLeague() throws Exception{
		String json = mapper.writeValueAsString(league1);
		when(leagueService.updateLeague((League)anyObject())).thenReturn(league1);
		mockMvc.perform(put("/leagues/").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(league1.getId())))
		.andExpect(jsonPath("$.leagueName", is(league1.getLeagueName())))
		.andDo(print());
	}

	@Test
	public void testGetLeaguesForPlayer() throws Exception {
		Set<LeagueName> list = new HashSet<LeagueName>();
		list.add(new LeagueName(league1));
		when(leagueService.getLeaguesForPlayer(anyString())).thenReturn(list);
		mockMvc.perform(get("/leagues/player/playerId"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].leagueName", is(league1.getLeagueName())))
		.andExpect(jsonPath("$[0].leagueId", is(league1.getId())))
		.andExpect(jsonPath("$", hasSize(1)))
		.andDo(print()).andReturn();
	}

	@Test
	public void testAddPlayerToLeague() throws Exception {
		String json = mapper.writeValueAsString(new PlayerLeague(new PlayerLeagueId("leagueId", "PlayerId")));
		mockMvc.perform(post("/leagues/player").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(principal))
		.andExpect(status().isOk())
		.andDo(print());
	}

	@Test
	public void testAddPlayerToLeagueAdmin() throws Exception {
		String json = mapper.writeValueAsString(new PlayerLeague(new PlayerLeagueId("leagueId", "PlayerId")));
		mockMvc.perform(post("/leagues/player/admin").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(principal))
		.andExpect(status().isOk())
		.andDo(print());
	}

	@Test
	public void testGetLeagueByName() throws Exception{
		when(leagueService.getLeagueByName(anyString())).thenReturn(league1);
		mockMvc.perform(get("/leagues/name/"+league1.getLeagueName()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(league1.getId())))
		.andExpect(jsonPath("$.leagueName", is(league1.getLeagueName())))
		.andDo(print());
	}

	@Test
	public void testRemovePlayerFromLeague() throws Exception {
		String json = mapper.writeValueAsString(new PlayerLeague(new PlayerLeagueId("leagueId", "PlayerId")));
		mockMvc.perform(delete("/leagues/player").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).principal(principal))
		.andExpect(status().isOk())
		.andDo(print());
	}

	@Test
	public void testGetPlayersInLeague() throws Exception {
		Set<String> listOfPlayers = new HashSet<String>();
		listOfPlayers.add("Player1");
		listOfPlayers.add("Player2");
		when(leagueService.getPlayersInLeague(anyString())).thenReturn(listOfPlayers);
		mockMvc.perform(get("/leagues/player/leagueid/"+league1.getLeagueName()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.[0]", is("Player2")))
		.andExpect(jsonPath("$.[1]", is("Player1")))
		.andDo(print());
	}

	@Test
	public void testDeleteLeague() throws Exception {
		assertThat(mockMvc.perform(delete("/leagues/leagueID").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).principal(principal))
				.andExpect(status().isOk())
				.andDo(print()));
	}
}
