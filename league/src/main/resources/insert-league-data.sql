insert into player_league (league_id, player_id, league_name,password) 
values (
'101Dharam', '1', 'IPL League','pass123'
);

insert into season (id, end_year, league_type, start_year)
values (
'IPL2017', '2017', 'pickem', '2016'
);

insert into league(id,league_name,admin_id,active,banker,double_enabled,double_type,entry_fee,fifth_place_percent,first_place_percent,fourth_place_percent,third_place_percent,second_place_percent,free,money,paid_for,spreads,weekly_fee,season_id,password) 
values(
 '101Dharam','IPL League',1,1,true,true,1,10,10,10,10,10,10,false,true,5000,true,100,'IPL2017','pass123'
);