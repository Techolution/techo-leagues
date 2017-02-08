package com.makeurpicks.domain;

public class OAuthClientDetailsBuilder {

	private String client_id;
	private String client_secret;
	private String authorities = "";
	private String authorized_grant_types = "";
	private String scope = "";
	private String web_server_redirect_uri = "";
	private String autoapprove;

	public OAuthClientDetailsBuilder(String client_id, String client_secret, String authorities,
			String authorized_grant_types, String scope, String web_server_redirect_uri, String autoapprove) {
		this.client_id = client_id;
		this.client_secret = client_secret;
		this.authorities = authorities;
		this.authorized_grant_types = authorized_grant_types;
		this.scope = scope;
		this.web_server_redirect_uri = web_server_redirect_uri;
		this.autoapprove = autoapprove;
	}

	public OAuthClientDetails build() {
		OAuthClientDetails client = new OAuthClientDetails();
		client.setClientId(client_id);
		client.setClient_secret(client_secret);
		client.setAuthorities(authorities);
		client.setAuthorized_grant_types(authorized_grant_types);
		client.setScope(scope);
		client.setWeb_server_redirect_uri(web_server_redirect_uri);
		client.setAutoapprove(autoapprove);
		client.setAccess_token_validity(3600);
		client.setRefresh_token_validity(3600);
		client.setResource_ids("");
		return client;
	}

}
