package com.makeurpicks.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class LeagueControllerITest {

	@Autowired
	WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	private final StringBuilder LEAGUE_DATA = new StringBuilder(
			"{\"id\":\"101Dharam\",\"leagueName\":\"IPL League new\",\"paidFor\":5000,")
					.append("\"money\":true,\"free\":false,\"active\":true,\"password\":\"pass123\",\"spreads\":true,")
					.append("\"doubleEnabled\":true,\"entryFee\":10.0,\"weeklyFee\":100.0,")
					.append("\"firstPlacePercent\":10,\"secondPlacePercent\":10,\"thirdPlacePercent\":10,")
					.append("\"fourthPlacePercent\":10,\"fifthPlacePercent\":10,\"doubleType\":1,\"banker\":true,\"seasonId\":\"IPL2017\",\"adminId\":\"1\"}");

	private final String PLAYER_DATA = "[{\"leagueName\":\"IPL League\",\"leagueId\":\"1\",\"seasonId\":\"1\"}]"; 
	
	@Test
	public void provideWAC_withServletContext_getLeagueController() {
		String controllerName = "leagueController";
		Assert.assertNotNull(context);
		Assert.assertNotNull(context.getBean(controllerName));
	}

	@Test
	public void initial_dataWouldBe_Blank() throws Exception {
		String baseUrl = "/";
		String expectedResult = "[]";
		String actualResult = this.mvc.perform(get(baseUrl)).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();
		Assert.assertEquals(expectedResult, actualResult);
	}

	@Test
	public void getLeagueType_responseOk_withPickemSuicide() throws Exception {
		String url = "/types";
		String expectedResult = "[\"pickem\",\"suicide\"]";
		this.mvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().string(expectedResult));
	}

	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
			@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void getLeagueById_leagueIdExists_responseOK() throws Exception {
		String id = "101Dharam";
		String url = "/{id}";

		this.mvc.perform(get(url, id)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is("101Dharam"))).andExpect(jsonPath("$.leagueName", is("IPL League")))
				.andExpect(jsonPath("$.paidFor", is(5000))).andExpect(jsonPath("$.money", is(true)))
				.andExpect(jsonPath("$.free", is(false))).andExpect(jsonPath("$.active", is(true)))
				.andExpect(jsonPath("$.password", is("pass123"))).andDo(print()).andReturn();
	}

	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
			@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void getLeagueById_noLeagueIdExists_responseOK() throws Exception {
		this.mvc.perform(get("/{id}", "2")).andExpect(status().isOk()).andExpect(content().string(""));
	}

	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
			@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void getLeagueBySeasonId_seasonIdExits_responseOk() throws Exception {
		String id = "IPL2017";
		String url = "/seasons/{seasonId}";

		this.mvc.perform(get(url, id)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id", is("101Dharam")))
				.andExpect(jsonPath("$[0].leagueName", is("IPL League"))).andExpect(jsonPath("$[0].paidFor", is(5000)))
				.andExpect(jsonPath("$[0].money", is(true))).andExpect(jsonPath("$[0].free", is(false)))
				.andExpect(jsonPath("$[0].active", is(true))).andExpect(jsonPath("$[0].password", is("pass123")))
				.andDo(print()).andReturn();
	}

	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
			@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void getLeagueBySeasonId_noSeasonIdExists_responseOK() throws Exception {
		String id = "2";
		String url = "/seasons/{seasonId}";
		this.mvc.perform(get(url, id)).andExpect(status().isOk()).andExpect(content().string("[]"));
	}

	@Test
	@SqlGroup({ @Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void createLeague_toBeCreatedLeague_responseOk() throws Exception {
		this.mvc.perform(post("/").principal(() -> "TEST_PRINCIPAL").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(LEAGUE_DATA.toString())).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("101Dharam"));
	}

	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void updateLeague_toBeUpdated_responseOk() throws Exception{
		
		 /*System.err.println("test: "+ this.mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON_VALUE).content(LEAGUE_DATA.toString()))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString());*/
		
		this.mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON_VALUE).content(LEAGUE_DATA.toString()))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.leagueName").value("IPL League new"));
	}
	
	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void getLeaguesForPlayer_playerIdExits_responseOk() throws Exception{
		String id = "1";
		String url = "/player/{id}";
		
		this.mvc.perform(get(url, id).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(jsonPath("$[0].leagueName").value("IPL League")); 
	}
	
	@Test
	public void getLeaguesForPlayer_playerIdNotExits_responseOk() throws Exception{
		String id = "2";
		String url = "/player/{id}";
		
		this.mvc.perform(get(url, id).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().string("[]")); 
	}
	
	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void getLeagueByName_leagueExists_responseOk() throws Exception {
		String leagueName = "IPL League";
		String url = "/name/{name}";
		
		this.mvc.perform(get(url, leagueName).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(jsonPath("$.leagueName", is("IPL League")))
		.andExpect(jsonPath("$.active", is(true)));
	}
	
	@Test
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void getPlayersInLeague_playerExists_responseOk() throws Exception {
		String leagueId = "101Dharam";
		String url = "/player/leagueid/{leagueid}";
		
		/*System.err.println("test: "+ this.mvc.perform(get(url, leagueId).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk()).andReturn().getResponse().getContentAsString());*/
		
		this.mvc.perform(get(url, leagueId).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
		//.andExpect(jsonPath("$[0]", is("1")));
		.andExpect(jsonPath("$[0]", is("1")));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"}) 
	@SqlGroup({ @Sql(scripts = "/insert-league-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "/delete-league-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) })
	public void deleteLeague_leagueIdExists_responseOk() throws Exception {
		String leagueId = "101Dharam";
		String url = "/{id}";
		
		/*System.err.println("test: "+ this.mvc.perform(delete(url, leagueId).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk()).andReturn().getResponse().getContentAsString());*/
		
		this.mvc.perform(delete(url, leagueId).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().string("true"));
	}
}
