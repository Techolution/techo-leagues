SET SQL_SAFE_UPDATES = 0;

delete from player_league where league_id='101Dharam';
delete from player_league where player_id='1';
delete from league where id='101Dharam';
delete from league where season_id='IPL2017';
delete from season where id='IPL2017';

commit;