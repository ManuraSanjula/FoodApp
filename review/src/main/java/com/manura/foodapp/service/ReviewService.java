package com.manura.foodapp.service;

import com.manura.foodapp.BadReqError;
import com.manura.foodapp.RedissonConfig;
import com.manura.foodapp.entity.ReviewEntity;
import com.manura.foodapp.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.modelmapper.ModelMapper;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

@Stateless
public class ReviewService {

    RedissonClient redisClient;
    RedissonConfig config = new RedissonConfig();

    @Inject
    @ConfigProperty(name = "userserviceurl")
    private String userserviceurl;
    private Client client;
    private WebTarget webTarget;
    private ModelMapper modelMapper = new ModelMapper();
    @Inject
    private ReviewService reviewService;

    @PreDestroy
    private void destroy() {
        if (client != null) {
            client.close();
        }

    }

    @PostConstruct
    public void init() {
        redisClient = config.getClient();
        client = ClientBuilder.newBuilder().connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        webTarget = client.target(userserviceurl);
    }

    public UserEntity getUserCacheEmpty(String email, String token) {
        JsonValue jsonValue = webTarget.path("{email}")
                .resolveTemplate("email", email).request(MediaType.APPLICATION_JSON)
                .header("Authorization", token).get(JsonValue.class);
        JsonObject jsonObject = jsonValue.asJsonObject();
        UserEntity userDto = new UserEntity();
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        String useremail = jsonObject.getString("email");
        String pic = jsonObject.getString("pic");

        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(useremail);

        userDto.setPic(pic);
        reviewService.saveUser(userDto);
        return userDto;
    }

    public UserEntity saveUser(UserEntity dto) {
        RBucket<Object> bucket = redisClient.getBucket(dto.getEmail());
        bucket.set(dto);
        return dto;
    }

    public UserEntity getUser(String email,String token) {
        RBucket<Object> bucket = redisClient.getBucket(email);
        UserEntity user = (UserEntity) bucket.get();
        if(user == null){
            return getUserCacheEmpty(email, token);
        }
        return user;
    }

    public List<ReviewEntity> getAllComments() {
        List<ReviewEntity> reviewEntitys = new ArrayList<>();
        RList<ReviewEntity> list = redisClient.getList("comments");
        list.forEach(i -> {
            reviewEntitys.add(i);
        });
        return reviewEntitys;
    }

    public ReviewEntity saveComment(String comment, String email,String token) {
        UserEntity user = null;
       if(token !=null){
            user = getUser(email,token);
        if(user == null){
          user = this.getUserCacheEmpty(email, token);
        }
        ReviewEntity entity = new ReviewEntity();
        entity.setComment(comment);
        entity.setUserEntity(user);
       
        RList<ReviewEntity> list = redisClient.getList("comments");
        list.add(entity);
        return entity;
       }else{
           throw new BadReqError("No Token Found");
       }
    }

}
