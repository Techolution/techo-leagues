package com.makeurpicks.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Authority implements GrantedAuthority{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4070053428969346269L;

	@GeneratedValue
	@Id
	private Integer id;
	
	private String authority;
	
	@ManyToOne
	@JoinColumn(name="PLAYER_ID", nullable=false)
	private Player player;
	
	@Override
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	

}
