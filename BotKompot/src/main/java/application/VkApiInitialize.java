package application;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;

public class VkApiInitialize {

	private final VkApiClient vk;

	public VkApiInitialize(VkApiClient vk) {
		super();
		this.vk = vk;
	}

	public UserActor initUserActor(int userId, String code) {
		// UserAuthResponse authResponse;
		UserActor actor = null;
		// authResponse = vk.oauth()
		// .userAuthorizationCodeFlow(clientId, clientSecret, redirectUri, code)
		// .execute();

		actor = new UserActor(userId, code);
		return actor;
	}

	public GroupActor initGroupActor(int groupId, String code) {
		// GroupAuthResponse authResponse;
		GroupActor actor = null;
		// try {
		// authResponse = vk.oauth()
		// .groupAuthorizationCodeFlow(clientId, clientSecret, redirectUri,
		// code)
		// .execute();
		actor = new GroupActor(groupId, code);
		// } catch (ApiException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClientException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return actor;
	}

	public ServiceActor initServiceClientActor(int clientId, String clientSecret, String code) {
		ServiceClientCredentialsFlowResponse authResponse;
		ServiceActor actor = null;
		try {
			authResponse = vk.oauth().serviceClientCredentialsFlow(clientId, code).execute();
			actor = new ServiceActor(clientId, clientSecret, authResponse.getAccessToken());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return actor;
	}
}
