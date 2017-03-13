package com.makeurpicks.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.makeurpicks.LeagueApplication;
import com.makeurpicks.controller.LeagueController;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = LeagueApplication.class, initializers = ConfigFileApplicationContextInitializer.class)
public class LeagueIntegrationTest {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@InjectMocks
	private LeagueController leagueController = new LeagueController();

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getAllLeagueTest() throws Exception {
		this.mockMvc.perform(get("/").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(content().json("[]"));
	}

	@Test
	public void getLeagueTypesTest() throws Exception {
		this.mockMvc.perform(get("/types").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(content().json("[pickem, suicide]"));
	}

	// Returning Null hence need to check
	@Test
	public void getLeagueByIdTest() throws Exception {
		this.mockMvc.perform(get("/1").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(null);
	}

	@Test
	public void getLeagueBySeasonIdTest() throws Exception {
		this.mockMvc.perform(get("/seasons/1").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(content().json("[]"));
	}

	@Test
	public void createLeagueTest() throws Exception {
		// Creating Object
		League league = new LeagueBuilder().withAdminId("1").withName("pickem").withPassword("football")
				.withSeasonId("1").build();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(league);

		// Creating User for authentication
		User user = new User("user", "", AuthorityUtils.createAuthorityList("ADMIN"));
		TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

		this.mockMvc.perform(
				post("/").principal(testingAuthenticationToken).contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isOk());
	}

	// Failing since record is not in database
	@Test
	public void updateLeagueTest() throws Exception {
		// Creating Object
		League league = new LeagueBuilder().withAdminId("1").withName("pickem").withPassword("football")
				.withSeasonId("1").build();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(league);

		this.mockMvc.perform(put("/").contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getLeaguesForPlayerTest() throws Exception {
		this.mockMvc.perform(get("/player/1").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(content().json("[]"));
	}

	@Test
	public void addPlayerToLeagueTest() throws Exception {
		// creating object
		PlayerLeagueId playerLeagueId = new PlayerLeagueId("leagueId", "playerId");
		PlayerLeague playerLeague = new PlayerLeague(playerLeagueId);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(playerLeague);

		// Creating User for authentication
		User user = new User("user", "", AuthorityUtils.createAuthorityList("ADMIN"));
		TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

		// Bad request since validations are failing
		this.mockMvc.perform(post("/player").principal(testingAuthenticationToken).contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)).andExpect(status().isBadRequest());
	}

	// Returning Null hence need to check
	@Test
	public void getLeagueByNameTest() throws Exception {
		this.mockMvc.perform(get("/name/admin").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(content().json("[]"));
	}

	@Test
	public void removePlayerFromLeagyeTest() throws Exception {
		// creating object
		PlayerLeagueId playerLeagueId = new PlayerLeagueId("leagueId", "playerId");
		PlayerLeague playerLeague = new PlayerLeague(playerLeagueId);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(playerLeague);

		this.mockMvc.perform(delete("/player").contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getPlayersInLeagueTest() throws Exception {
		this.mockMvc
				.perform(get("/player/leagueid/1").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(content().json("[]"));
	}

	@Test
	public void deleteLeagueTest() throws Exception {
		// Creating User for authentication
		User user = new User("ADMIN", "", AuthorityUtils.createAuthorityList("ADMIN"));
		TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

		// this.mockMvc.perform(delete("/1").principal(testingAuthenticationToken).contentType(APPLICATION_JSON_UTF8)).andExpect(status().isBadRequest());
	}
}
