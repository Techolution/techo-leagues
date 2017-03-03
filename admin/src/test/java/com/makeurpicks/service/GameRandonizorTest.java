package com.makeurpicks.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/*@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdminApplication.class)
@IntegrationTest*/

@RunWith(MockitoJUnitRunner.class)
public class GameRandonizorTest {

//	@Autowired 
	@Mock
	private GameRandonizor gameRandomizer;
	
	@Test
	public void test() {

		gameRandomizer.createRandomLeague(17);
	}

}
