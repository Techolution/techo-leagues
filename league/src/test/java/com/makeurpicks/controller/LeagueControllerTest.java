package com.makeurpicks.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.makeurpicks.domain.League;
import com.makeurpicks.repository.LeagueRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
/**
 * 
 * @author Santosh Kumar Kar
 * 
 * Integration test cases for LeagueController
 *
 */
public class LeagueControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private WebApplicationContext ctx;

	private Principal principal;
	
	@Before
	public void init() {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		
		principal = new Principal() {
	        @Override
	        public String getName() {
	            return "TEST_PRINCIPAL";
	        }
	    };
	    
	    League league1 = new League();
		league1.setId("1");
		league1.setLeagueName("league_1");
		league1.setActive(true);
		league1.setSeasonId("seasonn_1");
		league1.setAdminId("TEST_PRINCIPAL");
		league1.setPassword("admin1");
		leagueRepository.save(league1);
		
		League league2 = new League();
		league2.setId("2");
		league2.setLeagueName("league_2");
		league2.setActive(true);
		league2.setSeasonId("seasonn_2");
		league2.setAdminId("TEST_PRINCIPAL");
		league2.setPassword("admin2");
		leagueRepository.save(league2);
		
		
	}

	@Test
	public void createLeague() {
		
		League league = new League();
		league.setId("3");
		league.setLeagueName("league_3");
		league.setActive(true);
		league.setSeasonId("seasonn_3");
		league.setPassword("admin3");
		
		String URL = "/";

		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(league);
			MvcResult r = mockMvc.perform(
					post(URL).content(jsonStr).contentType("application/json;charset=UTF-8").principal(principal))
					.andExpect(status().isOk()).andDo(print())
					.andExpect(jsonPath("$.id", equalTo("3")))
					.andExpect(jsonPath("$.leagueName", equalTo("league_3")))
					.andExpect(jsonPath("$.seasonId", equalTo("seasonn_3")))
					.andExpect(jsonPath("$.password", equalTo("admin3")))
					.andExpect(jsonPath("$.adminId", equalTo("TEST_PRINCIPAL")))
					.andReturn();
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void getAllLeagues_returnsArray() {

		System.out.println(leagueRepository);
		String URL = "/";
		
		try {
			mockMvc.perform(get(URL)).andExpect(status().isOk()).andDo(print())
					.andExpect(jsonPath("$[0].id", equalTo("1")))
					.andExpect(jsonPath("$[0].leagueName", equalTo("league_1")))
					.andExpect(jsonPath("$[0].seasonId", equalTo("seasonn_1")))
					.andExpect(jsonPath("$[0].password", equalTo("admin1")))
					.andExpect(jsonPath("$[1].id", equalTo("2")))
					.andExpect(jsonPath("$[1].leagueName", equalTo("league_2")))
					.andExpect(jsonPath("$[1].seasonId", equalTo("seasonn_2")))
					.andExpect(jsonPath("$[1].password", equalTo("admin2")))			
					.andReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getLeagueById_firstRecord() {

		System.out.println(leagueRepository);
		String URL = "/1";

		try {
			mockMvc.perform(get(URL)).andExpect(status().isOk()).andDo(print())
					.andExpect(jsonPath("$.id", equalTo("1")))
					.andExpect(jsonPath("$.leagueName", equalTo("league_1")))
					.andExpect(jsonPath("$.seasonId", equalTo("seasonn_1")))
					.andExpect(jsonPath("$.password", equalTo("admin1")))
					.andReturn();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getLeagueById_secondRecord() {

		System.out.println(leagueRepository);
		String URL = "/2";

		try {
			mockMvc.perform(get(URL)).andExpect(status().isOk()).andDo(print())
					.andExpect(jsonPath("$.id", equalTo("2")))
					.andExpect(jsonPath("$.leagueName", equalTo("league_2")))
					.andExpect(jsonPath("$.seasonId", equalTo("seasonn_2")))
					.andExpect(jsonPath("$.password", equalTo("admin2")))
					.andReturn();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}