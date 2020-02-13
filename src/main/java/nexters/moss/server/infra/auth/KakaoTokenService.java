package nexters.moss.server.infra.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nexters.moss.server.domain.service.SocialTokenService;
import nexters.moss.server.domain.model.exception.SocialUserInfoException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KakaoTokenService implements SocialTokenService {
    private  String requestUrl;

    // TODO: export to config file
    public KakaoTokenService() {
        this.requestUrl = "https://kapi.kakao.com/v2/user/me";
    }

    @Override
    public Long getSocialUserId(String accessToken) throws SocialUserInfoException {
        return httpTemplate(accessToken, (client, mapper) -> {
            HttpPost post = new HttpPost(requestUrl);
            post.addHeader("Authorization", "Bearer " + accessToken);

            HttpResponse response = client.execute(post);
            JsonNode jsonNode = mapper.readTree(response.getEntity().getContent());

            return jsonNode.path("id").asLong();
        });
    }

    private <T>T httpTemplate(String accessToken, HttpCallback<T> callback) {
        HttpClient client = HttpClientBuilder.create().build();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return callback.getHttpData(client, mapper);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SocialUserInfoException("No Matched User with Social ID");
        }
    }
}
