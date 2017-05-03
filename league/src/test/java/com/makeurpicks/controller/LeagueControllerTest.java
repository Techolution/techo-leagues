package com.makeurpicks.controller;

import com.google.gson.Gson;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class LeagueControllerTest {


    private final String LEAGUE_DATA = "{\"id\":\"1\",\"leagueName\":\"test league\",\"paidFor\":0," +
            "\"money\":true,\"free\":false,\"active\":true,\"password\":\"test\",\"spreads\":true," +
            "\"doubleEnabled\":true,\"entryFee\":10.0,\"weeklyFee\":100.0," +
            "\"firstPlacePercent\":10,\"secondPlacePercent\":10,\"thirdPlacePercent\":10," +
            "\"fourthPlacePercent\":10,\"fifthPlacePercent\":10,\"doubleType\":1,\"banker\":true,\"seasonId\":\"1\",\"adminId\":\"1\"}";

    private final String LEAGUES_DATA = "[{\"id\":\"1\",\"leagueName\":\"test league\",\"paidFor\":0," +
            "\"money\":true,\"free\":false,\"active\":true,\"password\":\"test\",\"spreads\":true," +
            "\"doubleEnabled\":true,\"entryFee\":10.0,\"weeklyFee\":100.0," +
            "\"firstPlacePercent\":10,\"secondPlacePercent\":10,\"thirdPlacePercent\":10," +
            "\"fourthPlacePercent\":10,\"fifthPlacePercent\":10,\"doubleType\":1,\"banker\":true,\"seasonId\":\"1\",\"adminId\":\"1\"}]";

    private final String PLAYER_DATA = "[{\"leagueName\":\"test league\",\"leagueId\":\"1\",\"seasonId\":\"1\"}]";

    private Gson gson = new Gson();

    private League league;


    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        league = gson.fromJson(LEAGUE_DATA, League.class);
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesLeagueController() {
        ServletContext servletContext = wac.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("leagueController"));
    }


    @Test
    public void givenBaseURI_WhenNoLeaguesExist_thenResponseOkWithNoContent() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenBaseURI_WhenLeaguesExist_ExpectAllLeaguesToBeReturned() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void givenLeagueTypesUriWithGet_thenResponseOk() throws Exception {
        this.mockMvc.perform(get("/types"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("[\"pickem\",\"suicide\"]"));

    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenGetLeagueByIDUri_WhenLeagueIdNotExists_thenResponseOk() throws Exception {

        this.mockMvc.perform(get("/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(LEAGUE_DATA));

    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenGetLeagueByIDUri_WhenLeagueIdExists_thenResponseOk() throws Exception {

        this.mockMvc.perform(get("/{id}", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenGetSeasonByIDUri_WhenSeasonIdExists_thenResponseOk() throws Exception {

        this.mockMvc.perform(get("/seasons/{seasonId}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(LEAGUES_DATA));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenGetSeasonByIDUri_WhenSeasonIdNotExists_thenResponseOk() throws Exception {

        this.mockMvc.perform(get("/seasons/{seasonId}", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

    }

    @Test
    @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    public void givenBaseUriWithPost_ExpectsLeagueToBeCreated() throws Exception {
        this.mockMvc.perform(post("/").principal(() -> "TEST_PRINCIPAL")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(LEAGUE_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenBaseUriWithPut_ExpectsLeagueToBeUpdated() throws Exception {

        this.mockMvc.perform(put("/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(LEAGUE_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("test league"));
    }


    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/insert-player-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-player-data.sql", executionPhase = AFTER_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenPlayerUriWithGet_WhenPlayerIdExists_ReturnsPlayerLeague() throws Exception {

        this.mockMvc.perform(get("/player/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(PLAYER_DATA));

    }

    @Test
    public void givenPlayerUriWithGet_WhenPlayerIdNotExists_ExpectsEmptyArray() throws Exception {

        this.mockMvc.perform(get("/player/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("[]"));

    }


    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenLeagueNameURIWithGet_WhenLeagueNameExists_ExpectsLeagueToBeReturned() throws Exception {

        this.mockMvc.perform(get("/name/{name}", "test league")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(LEAGUE_DATA));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
    })
    public void givenDeleteLeagueUriWithId_whenIdExists_thenVerifyResponseOk() throws Exception {

        this.mockMvc.perform(delete("/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/insert-league-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/insert-player-data.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "/delete-league-data.sql", executionPhase = AFTER_TEST_METHOD)
    })
    public void givenDeletePlayerLeagueUriWithId_whenIdExists_thenVerifyResponseOk() throws Exception {

        final PlayerLeagueId playerLeagueId = new PlayerLeagueId("1", "1");
        final PlayerLeague playerLeague =  new PlayerLeague(playerLeagueId);

        this.mockMvc.perform(delete("/player")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(playerLeague)))
                .andExpect(status().isOk());
    }

}
