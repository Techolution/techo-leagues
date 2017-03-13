package com.makeurpicks.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

@Entity
@Table(name = "oauth_client_details")
public class OAuthClientDetails implements ClientDetails {

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public Set<String> getResourceIds() {
		// TODO Auto-generated method stub
		return getResource_ids();
	}

	@Override
	public boolean isSecretRequired() {
		return true;
	}

	@Override
	public String getClientSecret() {
		return client_secret;
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return getAuthorized_grant_types();
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return getWeb_server_redirect_uri();
	}

	@Override
	public Integer getAccessTokenValiditySeconds() {
		// TODO Auto-generated method stub
		return access_token_validity;
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		// TODO Auto-generated method stub
		return refresh_token_validity;
	}

	@Override
	public boolean isAutoApprove(String scope) {
		// TODO Auto-generated method stub
		return Boolean.valueOf(autoapprove);
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		return null;
	}

	@Id
	@Column(name = "client_id")
	private String clientId;
	private String client_secret;
	private boolean isScoped;
	private Integer access_token_validity;
	private Integer refresh_token_validity;
	private String autoapprove;
	private String resource_ids = "";
	private String scope = "";
	private String authorized_grant_types = "";
	private String web_server_redirect_uri = "";
	private String authorities = "";
	@Column(name = "additional_information")
	private String additionalInformation;

	public void setClientId(String client_id) {
		this.clientId = client_id;
	}

	public Set<String> getResource_ids() {
		HashSet<String> s = new HashSet<String>();
		StringTokenizer t = new StringTokenizer(resource_ids, ",");
		while (t.hasMoreTokens()) {
			String n = t.nextToken();
			s.add(n);
		}
		return s;
	}

	public void setResource_ids(String resource_ids) {
		this.resource_ids = resource_ids;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public Set<String> getScope() {
		HashSet<String> s = new HashSet<String>();
		StringTokenizer t = new StringTokenizer(scope, ",");
		while (t.hasMoreTokens()) {
			String n = t.nextToken();
			s.add(n);
		}
		return s;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public boolean isScoped() {
		return isScoped;
	}

	public void setScoped(boolean isScoped) {
		this.isScoped = isScoped;
	}

	public Set<String> getAuthorized_grant_types() {
		HashSet<String> s = new HashSet<String>();
		StringTokenizer t = new StringTokenizer(authorized_grant_types, ",");
		while (t.hasMoreTokens()) {
			String n = t.nextToken();
			s.add(n);
		}
		return s;
	}

	public void setAuthorized_grant_types(String authorized_grant_types) {
		this.authorized_grant_types = authorized_grant_types;
	}

	public Set<String> getWeb_server_redirect_uri() {
		HashSet<String> s = new HashSet<String>();
		StringTokenizer t = new StringTokenizer(web_server_redirect_uri, ",");
		while (t.hasMoreTokens()) {
			String n = t.nextToken();
			s.add(n);
		}
		return s;
	}

	public void setWeb_server_redirect_uri(String web_server_redirect_uri) {
		this.web_server_redirect_uri = web_server_redirect_uri;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public Integer getAccess_token_validity() {
		return access_token_validity;
	}

	public void setAccess_token_validity(Integer access_token_validity) {
		this.access_token_validity = access_token_validity;
	}

	public Integer getRefresh_token_validity() {
		return refresh_token_validity;
	}

	public void setRefresh_token_validity(Integer refresh_token_validity) {
		this.refresh_token_validity = refresh_token_validity;
	}

	public String getAutoapprove() {
		return autoapprove;
	}

	public void setAutoapprove(String autoapprove) {
		this.autoapprove = autoapprove;
	}

	public OAuthClientDetails() {
	}

	public OAuthClientDetails(String client_id, String resource_ids, String client_secret, String scope,
			boolean isScoped, String authorized_grant_types, String web_server_redirect_uri, String authorities,
			Integer access_token_validity, Integer refresh_token_validity, String autoapprove) {
		this.clientId = client_id;
		this.resource_ids = resource_ids;
		this.client_secret = client_secret;
		this.scope = scope;
		this.isScoped = isScoped;
		this.authorized_grant_types = authorized_grant_types;
		this.web_server_redirect_uri = web_server_redirect_uri;
		this.authorities = authorities;
		this.access_token_validity = access_token_validity;
		this.refresh_token_validity = refresh_token_validity;
		this.autoapprove = autoapprove;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
		HashSet<String> s = new HashSet<String>();
		StringTokenizer t = new StringTokenizer(authorities, ",");
		while (t.hasMoreTokens()) {
			String n = t.nextToken();
			SimpleGrantedAuthority g = new SimpleGrantedAuthority(n);
			auths.add(g);
		}
		return auths;
	}

}
